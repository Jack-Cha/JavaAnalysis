package com.javaanalysis;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Analyzes Java source files and extracts class information
 */
public class JavaSourceAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(JavaSourceAnalyzer.class);
    private final JavaParser javaParser;
    private final Map<String, ClassInfo> classInfoMap;

    public JavaSourceAnalyzer() {
        this.javaParser = new JavaParser();
        this.classInfoMap = new HashMap<>();
    }

    /**
     * Analyzes all Java files in the given directory
     */
    public Map<String, ClassInfo> analyzeDirectory(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory: " + directoryPath);
        }

        logger.info("Analyzing Java files in directory: {}", directoryPath);

        List<Path> javaFiles = findJavaFiles(directory.toPath());
        logger.info("Found {} Java files", javaFiles.size());

        for (Path javaFile : javaFiles) {
            try {
                analyzeFile(javaFile);
            } catch (Exception e) {
                logger.error("Error analyzing file: {}", javaFile, e);
            }
        }

        return classInfoMap;
    }

    /**
     * Finds all .java files in the directory recursively
     */
    private List<Path> findJavaFiles(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Analyzes a single Java file
     */
    private void analyzeFile(Path filePath) throws IOException {
        logger.debug("Analyzing file: {}", filePath);

        ParseResult<CompilationUnit> parseResult = javaParser.parse(filePath);

        if (!parseResult.isSuccessful()) {
            logger.warn("Failed to parse file: {}", filePath);
            parseResult.getProblems().forEach(problem ->
                    logger.warn("Parse problem: {}", problem.getMessage()));
            return;
        }

        CompilationUnit cu = parseResult.getResult().orElse(null);
        if (cu == null) {
            return;
        }

        String packageName = cu.getPackageDeclaration()
                .map(pd -> pd.getNameAsString())
                .orElse("");

        // Process classes
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
            processClassDeclaration(classDecl, packageName);
        });

        // Process enums
        cu.findAll(EnumDeclaration.class).forEach(enumDecl -> {
            processEnumDeclaration(enumDecl, packageName);
        });
    }

    /**
     * Processes a class or interface declaration
     */
    private void processClassDeclaration(ClassOrInterfaceDeclaration classDecl, String packageName) {
        String className = classDecl.getNameAsString();
        ClassInfo classInfo = new ClassInfo(className, packageName);

        classInfo.setInterface(classDecl.isInterface());
        classInfo.setAbstract(classDecl.isAbstract());

        // Process superclass
        classDecl.getExtendedTypes().forEach(extendedType -> {
            String superClassName = extendedType.getNameAsString();
            classInfo.setSuperClass(superClassName);
            classInfo.addDependency(superClassName);
        });

        // Process implemented interfaces
        classDecl.getImplementedTypes().forEach(implementedType -> {
            String interfaceName = implementedType.getNameAsString();
            classInfo.addInterface(interfaceName);
            classInfo.addDependency(interfaceName);
        });

        // Process fields
        classDecl.getFields().forEach(field -> {
            processField(field, classInfo);
        });

        // Process methods
        classDecl.getMethods().forEach(method -> {
            processMethod(method, classInfo);
        });

        // Process constructors
        classDecl.getConstructors().forEach(constructor -> {
            processConstructor(constructor, classInfo);
        });

        classInfoMap.put(classInfo.getFullName(), classInfo);
        logger.debug("Processed class: {}", classInfo.getFullName());
    }

    /**
     * Processes an enum declaration
     */
    private void processEnumDeclaration(EnumDeclaration enumDecl, String packageName) {
        String enumName = enumDecl.getNameAsString();
        ClassInfo classInfo = new ClassInfo(enumName, packageName);
        classInfo.setEnum(true);

        // Process implemented interfaces
        enumDecl.getImplementedTypes().forEach(implementedType -> {
            String interfaceName = implementedType.getNameAsString();
            classInfo.addInterface(interfaceName);
            classInfo.addDependency(interfaceName);
        });

        // Process enum constants as fields
        enumDecl.getEntries().forEach(entry -> {
            FieldInfo fieldInfo = new FieldInfo(
                    entry.getNameAsString(),
                    enumName,
                    "public"
            );
            classInfo.addField(fieldInfo);
        });

        classInfoMap.put(classInfo.getFullName(), classInfo);
        logger.debug("Processed enum: {}", classInfo.getFullName());
    }

    /**
     * Processes a field declaration
     */
    private void processField(FieldDeclaration field, ClassInfo classInfo) {
        String visibility = getVisibility(field.getModifiers());
        String fieldType = field.getCommonType().asString();

        // Add type as dependency
        addTypeDependency(fieldType, classInfo);

        field.getVariables().forEach(variable -> {
            FieldInfo fieldInfo = new FieldInfo(
                    variable.getNameAsString(),
                    fieldType,
                    visibility
            );
            classInfo.addField(fieldInfo);
        });
    }

    /**
     * Processes a method declaration
     */
    private void processMethod(MethodDeclaration method, ClassInfo classInfo) {
        String methodName = method.getNameAsString();
        String returnType = method.getType().asString();
        String visibility = getVisibility(method.getModifiers());

        MethodInfo methodInfo = new MethodInfo(methodName, returnType, visibility);
        methodInfo.setStatic(method.isStatic());
        methodInfo.setAbstract(method.isAbstract());

        // Add return type as dependency
        addTypeDependency(returnType, classInfo);

        // Process parameters
        method.getParameters().forEach(parameter -> {
            String paramType = parameter.getType().asString();
            String paramName = parameter.getNameAsString();

            ParameterInfo paramInfo = new ParameterInfo(paramName, paramType);
            methodInfo.addParameter(paramInfo);

            // Add parameter type as dependency
            addTypeDependency(paramType, classInfo);
        });

        classInfo.addMethod(methodInfo);
    }

    /**
     * Processes a constructor declaration
     */
    private void processConstructor(ConstructorDeclaration constructor, ClassInfo classInfo) {
        String visibility = getVisibility(constructor.getModifiers());

        MethodInfo methodInfo = new MethodInfo(
                classInfo.getClassName(),
                "",
                visibility
        );

        // Process parameters
        constructor.getParameters().forEach(parameter -> {
            String paramType = parameter.getType().asString();
            String paramName = parameter.getNameAsString();

            ParameterInfo paramInfo = new ParameterInfo(paramName, paramType);
            methodInfo.addParameter(paramInfo);

            // Add parameter type as dependency
            addTypeDependency(paramType, classInfo);
        });

        classInfo.addMethod(methodInfo);
    }

    /**
     * Determines the visibility of a member based on its modifiers
     */
    private String getVisibility(NodeList<Modifier> nodeList) {
        if (nodeList.contains(Modifier.publicModifier())) {
            return "public";
        } else if (nodeList.contains(Modifier.privateModifier())) {
            return "private";
        } else if (nodeList.contains(Modifier.protectedModifier())) {
            return "protected";
        } else {
            return "package-private";
        }
    }

    /**
     * Adds a type as a dependency if it's not a primitive type
     */
    private void addTypeDependency(String type, ClassInfo classInfo) {
        // Remove generic types and array brackets
        String cleanType = type.replaceAll("<.*>", "").replace("[]", "").trim();

        // Skip primitive types and common Java types
        if (!isPrimitiveOrCommonType(cleanType)) {
            classInfo.addDependency(cleanType);
        }
    }

    /**
     * Checks if a type is a primitive or common Java type
     */
    private boolean isPrimitiveOrCommonType(String type) {
        return type.matches("(byte|short|int|long|float|double|boolean|char|void)")
                || type.startsWith("java.lang.")
                || type.startsWith("java.util.");
    }

    public Map<String, ClassInfo> getClassInfoMap() {
        return classInfoMap;
    }
}
