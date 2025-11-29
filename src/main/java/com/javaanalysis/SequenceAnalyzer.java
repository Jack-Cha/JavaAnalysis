package com.javaanalysis;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SequenceAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(SequenceAnalyzer.class);
    private final JavaParser javaParser;
    private final String sourceRoot;
    private static final int MAX_DEPTH = 5; // Prevent infinite recursion

    public static class CallTrace {
        public String sourceClass;
        public String targetClass;
        public String methodName;
        public String returnType;
        public int depth;

        public CallTrace(String sourceClass, String targetClass, String methodName, String returnType, int depth) {
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
            this.methodName = methodName;
            this.returnType = returnType;
            this.depth = depth;
        }
    }

    public SequenceAnalyzer(String sourceRoot) {
        this.sourceRoot = sourceRoot;

        // Configure Symbol Solver to resolve types
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(new File(sourceRoot)));

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setSymbolResolver(symbolSolver);

        this.javaParser = new JavaParser(parserConfiguration);
    }

    public List<CallTrace> analyze(String className, String methodName) throws IOException {
        List<CallTrace> traces = new ArrayList<>();
        
        // Find the starting class file
        File startFile = findFileForClass(new File(sourceRoot), className);
        if (startFile == null) {
            logger.error("Could not find source file for class: {}", className);
            return traces;
        }

        ParseResult<CompilationUnit> parseResult = javaParser.parse(startFile);
        if (!parseResult.isSuccessful()) {
            logger.error("Failed to parse file: {}", startFile);
            return traces;
        }

        CompilationUnit cu = parseResult.getResult().get();
        
        // Find the starting method
        Optional<MethodDeclaration> startMethod = cu.findFirst(ClassOrInterfaceDeclaration.class, 
                c -> c.getNameAsString().equals(className))
                .flatMap(c -> c.findFirst(MethodDeclaration.class, 
                        m -> m.getNameAsString().equals(methodName)));

        if (startMethod.isPresent()) {
            logger.info("Analyzing sequence starting from {}.{}", className, methodName);
            analyzeMethodBody(startMethod.get(), className, traces, 0);
        } else {
            logger.error("Method {} not found in class {}", methodName, className);
        }

        return traces;
    }

    private File findFileForClass(File dir, String className) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    File found = findFileForClass(file, className);
                    if (found != null) return found;
                } else if (file.getName().equals(className + ".java")) {
                    return file;
                }
            }
        }
        return null;
    }

    private void analyzeMethodBody(MethodDeclaration method, String currentClass, List<CallTrace> traces, int depth) {
        if (depth >= MAX_DEPTH) return;

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodCallExpr n, Void arg) {
                super.visit(n, arg);

                try {
                    // Try to resolve the method call to find the target class
                    ResolvedMethodDeclaration resolvedMethod = n.resolve();
                    String targetClass = resolvedMethod.declaringType().getClassName();
                    String calledMethodName = resolvedMethod.getName();
                    String returnType = resolvedMethod.getReturnType().describe();
                    
                    // Filter out common Java classes to keep diagram clean (optional)
                    if (isSystemClass(targetClass)) {
                         // Skip java system classes for clarity, or keep them if preferred
                         // keeping them for now but maybe we can flag them
                    }

                    traces.add(new CallTrace(currentClass, targetClass, calledMethodName, returnType, depth));

                    // Recursively analyze the called method if it's in our source code
                    // This is complex because we need to find the source for the target class
                    // For now, we only trace one level deep or need to load the target CU
                    
                    // TODO: For a full depth analysis, we would need to load the target class CU and find the method
                    // This implementation focuses on the immediate calls from the start method for this iteration.
                    
                } catch (Exception e) {
                    // If resolution fails (e.g., library calls not in solver), just log partial info
                    logger.debug("Could not resolve method call: {}", n.getNameAsString());
                    traces.add(new CallTrace(currentClass, "Unknown", n.getNameAsString(), "void", depth));
                }
            }
        }, null);
    }

    private boolean isSystemClass(String className) {
        return className.startsWith("java.") || className.startsWith("javax.") || className.equals("String") || className.equals("Object");
    }
}
