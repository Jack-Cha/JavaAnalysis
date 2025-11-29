package com.javaanalysis;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.core.DiagramDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Generates PlantUML diagrams from class information
 */
public class PlantUMLGenerator {
    private static final Logger logger = LoggerFactory.getLogger(PlantUMLGenerator.class);
    
    static {
        // Force UTF-8 encoding for PlantUML to avoid CP949 issues on Windows
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("plantuml.charset", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        // Set PlantUML to use UTF-8 for all operations
        System.setProperty("PLANTUML_SECURITY_PROFILE", "UNSECURE");
    }

    /**
     * Generates PlantUML code from class information
     */
    public String generatePlantUML(Map<String, ClassInfo> classInfoMap) {
        StringBuilder uml = new StringBuilder();

        uml.append("@startuml\n");
        uml.append("skinparam classAttributeIconSize 0\n");
        uml.append("skinparam classFontSize 12\n");
        uml.append("skinparam packageStyle rectangle\n");
        uml.append("left to right direction\n\n");

        // Group classes by package
        Map<String, List<ClassInfo>> packageMap = groupByPackage(classInfoMap);

        // Generate class definitions
        for (Map.Entry<String, List<ClassInfo>> entry : packageMap.entrySet()) {
            String packageName = entry.getKey();
            List<ClassInfo> classes = entry.getValue();

            if (!packageName.isEmpty()) {
                uml.append("package \"").append(packageName).append("\" {\n");
            }

            for (ClassInfo classInfo : classes) {
                generateClassDefinition(classInfo, uml);
                uml.append("\n");
            }

            if (!packageName.isEmpty()) {
                uml.append("}\n\n");
            }
        }

        // Generate relationships
        generateRelationships(classInfoMap, uml);

        uml.append("@enduml\n");

        return uml.toString();
    }

    /**
     * Groups classes by package
     */
    private Map<String, List<ClassInfo>> groupByPackage(Map<String, ClassInfo> classInfoMap) {
        Map<String, List<ClassInfo>> packageMap = new TreeMap<>();

        for (ClassInfo classInfo : classInfoMap.values()) {
            String packageName = classInfo.getPackageName() != null
                    ? classInfo.getPackageName()
                    : "";

            packageMap.computeIfAbsent(packageName, k -> new ArrayList<>())
                    .add(classInfo);
        }

        return packageMap;
    }

    /**
     * Generates the class definition in PlantUML syntax
     */
    private void generateClassDefinition(ClassInfo classInfo, StringBuilder uml) {
        String indent = classInfo.getPackageName().isEmpty() ? "" : "  ";

        // Class declaration
        if (classInfo.isEnum()) {
            uml.append(indent).append("enum ").append(classInfo.getClassName());
        } else if (classInfo.isInterface()) {
            uml.append(indent).append("interface ").append(classInfo.getClassName());
        } else if (classInfo.isAbstract()) {
            uml.append(indent).append("abstract class ").append(classInfo.getClassName());
        } else {
            uml.append(indent).append("class ").append(classInfo.getClassName());
        }

        uml.append(" {\n");

        // Fields
        if (!classInfo.getFields().isEmpty()) {
            for (FieldInfo field : classInfo.getFields()) {
                uml.append(indent).append("  ").append(field.toString()).append("\n");
            }
        }

        // Separator between fields and methods
        if (!classInfo.getFields().isEmpty() && !classInfo.getMethods().isEmpty()) {
            uml.append(indent).append("  --\n");
        }

        // Methods
        if (!classInfo.getMethods().isEmpty()) {
            for (MethodInfo method : classInfo.getMethods()) {
                uml.append(indent).append("  ").append(method.toString()).append("\n");
            }
        }

        uml.append(indent).append("}\n");
    }

    /**
     * Generates relationships between classes
     */
    private void generateRelationships(Map<String, ClassInfo> classInfoMap, StringBuilder uml) {
        uml.append("\n' Relationships\n");

        for (ClassInfo classInfo : classInfoMap.values()) {
            String className = classInfo.getClassName();

            // Inheritance (extends)
            if (classInfo.getSuperClass() != null && !classInfo.getSuperClass().isEmpty()) {
                String superClass = getSimpleClassName(classInfo.getSuperClass());
                if (classExists(superClass, classInfoMap)) {
                    uml.append(superClass).append(" <|-- ").append(className).append("\n");
                }
            }

            // Interface implementation
            for (String interfaceName : classInfo.getInterfaces()) {
                String simpleInterfaceName = getSimpleClassName(interfaceName);
                if (classExists(simpleInterfaceName, classInfoMap)) {
                    uml.append(simpleInterfaceName).append(" <|.. ").append(className).append("\n");
                }
            }

            // Dependencies (associations)
            Set<String> processedDependencies = new HashSet<>();
            for (String dependency : classInfo.getDependencies()) {
                String simpleDependency = getSimpleClassName(dependency);

                // Skip if already processed, is superclass, or is interface
                if (processedDependencies.contains(simpleDependency)
                        || simpleDependency.equals(classInfo.getSuperClass())
                        || classInfo.getInterfaces().contains(dependency)) {
                    continue;
                }

                if (classExists(simpleDependency, classInfoMap) && !simpleDependency.equals(className)) {
                    uml.append(className).append(" ..> ").append(simpleDependency)
                            .append(" : uses\n");
                    processedDependencies.add(simpleDependency);
                }
            }
        }
    }

    /**
     * Extracts simple class name from fully qualified name
     */
    private String getSimpleClassName(String className) {
        if (className.contains(".")) {
            return className.substring(className.lastIndexOf('.') + 1);
        }
        return className;
    }

    /**
     * Checks if a class exists in the classInfoMap
     */
    private boolean classExists(String className, Map<String, ClassInfo> classInfoMap) {
        // Check by simple class name
        for (ClassInfo info : classInfoMap.values()) {
            if (info.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Saves PlantUML code to a file
     */
    public void savePlantUMLFile(String plantUMLCode, String outputPath) throws IOException {
        Path path = Paths.get(outputPath);
        Files.createDirectories(path.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(plantUMLCode);
        }

        logger.info("PlantUML file saved to: {}", outputPath);
    }

    /**
     * Generates an image file from PlantUML code
     */
    public void generateImage(String plantUMLCode, String outputPath, FileFormat format) throws IOException {
        Path outputFilePath = Paths.get(outputPath);
        Files.createDirectories(outputFilePath.getParent());

        try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
            // Create SourceStringReader with the PlantUML code as String
            // The string is already in Java's internal UTF-16 format, so no encoding issues
            SourceStringReader reader = new SourceStringReader(plantUMLCode);

            // Generate the diagram with specified format
            DiagramDescription description = reader.outputImage(outputStream, new FileFormatOption(format));

            if (description != null) {
                String desc = description.getDescription();
                logger.debug("PlantUML generation result: {}", desc);

                // Check for errors in the description
                if (desc != null && (desc.toLowerCase().contains("error") || desc.toLowerCase().contains("syntax"))) {
                    logger.error("PlantUML generation error: {}", desc);
                    throw new IOException("PlantUML failed to generate image: " + desc);
                }
            }
        }

        logger.info("UML diagram image saved to: {}", outputPath);
    }

    /**
     * Generates both PlantUML file and PNG image
     */
    public void generateDiagram(Map<String, ClassInfo> classInfoMap, String basePath) throws IOException {
        String plantUMLCode = generatePlantUML(classInfoMap);

        // Save PlantUML source
        String pumlPath = basePath + ".puml";
        savePlantUMLFile(plantUMLCode, pumlPath);

        // Generate PNG image
        String pngPath = basePath + ".png";
        generateImage(plantUMLCode, pngPath, FileFormat.PNG);

        // Generate SVG image (optional, better quality)
        String svgPath = basePath + ".svg";
        generateImage(plantUMLCode, svgPath, FileFormat.SVG);

        logger.info("UML diagrams generated successfully");
    }
}
