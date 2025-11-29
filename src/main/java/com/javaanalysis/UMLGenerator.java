package com.javaanalysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * Main application for generating UML diagrams from Java source code
 */
public class UMLGenerator {
    private static final Logger logger = LoggerFactory.getLogger(UMLGenerator.class);

    public static void main(String[] args) {
        // Set UTF-8 encoding to avoid CP949 issues on Windows
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        System.setProperty("plantuml.charset", "UTF-8");

        // Force default charset to UTF-8 using reflection (aggressive fix for Windows CP949)
        try {
            System.setProperty("file.encoding", "UTF-8");
            java.lang.reflect.Field charset = java.nio.charset.Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.warn("Could not set default charset to UTF-8 via reflection: {}", e.getMessage());
        }

        logger.info("=== Java Source UML Generator ===");
        logger.info("Default Charset: {}", java.nio.charset.Charset.defaultCharset());

        // Parse command line arguments
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }

        if (args[0].equals("-sequence")) {
            if (args.length < 4) {
                System.out.println("Error: Missing arguments for sequence diagram generation.");
                printUsage();
                System.exit(1);
            }
            generateSequenceDiagram(args);
        } else {
            generateClassDiagram(args);
        }
    }

    private static void generateSequenceDiagram(String[] args) {
        String sourceDirectory = args[1];
        String className = args[2];
        String methodName = args[3];
        String outputBasePath = args.length > 4 ? args[4] : "output/sequence-diagram";

        try {
            logger.info("Generating Sequence Diagram...");
            logger.info("Source: {}", sourceDirectory);
            logger.info("Entry Point: {}.{}", className, methodName);

            SequenceAnalyzer analyzer = new SequenceAnalyzer(sourceDirectory);
            java.util.List<SequenceAnalyzer.CallTrace> traces = analyzer.analyze(className, methodName);

            SequencePlantUMLGenerator generator = new SequencePlantUMLGenerator();
            generator.generateDiagram(className, methodName, traces, outputBasePath);

        } catch (Exception e) {
            logger.error("Error generating Sequence Diagram", e);
        }
    }

    private static void generateClassDiagram(String[] args) {
        String sourceDirectory = args[0];
        String outputBasePath = args.length > 1 ? args[1] : "output/uml-diagram";

        try {
            // Validate source directory
            File sourceDir = new File(sourceDirectory);
            if (!sourceDir.exists() || !sourceDir.isDirectory()) {
                logger.error("Source directory does not exist or is not a directory: {}", sourceDirectory);
                System.exit(1);
            }

            logger.info("Source directory: {}", sourceDirectory);
            logger.info("Output base path: {}", outputBasePath);

            // Analyze Java source files
            logger.info("\n--- Analyzing Java Source Files ---");
            JavaSourceAnalyzer analyzer = new JavaSourceAnalyzer();
            Map<String, ClassInfo> classInfoMap = analyzer.analyzeDirectory(sourceDirectory);

            if (classInfoMap.isEmpty()) {
                logger.warn("No Java classes found in the specified directory");
                System.exit(0);
            }

            logger.info("Found {} classes", classInfoMap.size());
            logger.info("\nClasses analyzed:");
            for (ClassInfo classInfo : classInfoMap.values()) {
                String type = classInfo.isInterface() ? "interface"
                        : classInfo.isEnum() ? "enum"
                        : classInfo.isAbstract() ? "abstract class"
                        : "class";
                logger.info("  - {} {}", type, classInfo.getFullName());
            }

            // Generate UML diagrams
            logger.info("\n--- Generating UML Diagrams ---");
            PlantUMLGenerator generator = new PlantUMLGenerator();
            generator.generateDiagram(classInfoMap, outputBasePath);

            logger.info("\n=== UML Generation Complete ===");
            logger.info("PlantUML file: {}.puml", outputBasePath);
            logger.info("PNG diagram: {}.png", outputBasePath);
            logger.info("SVG diagram: {}.svg", outputBasePath);

        } catch (Exception e) {
            logger.error("Error generating UML diagram", e);
            System.exit(1);
        }
    }

    /**
     * Prints usage information
     */
    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  Class Diagram:    java -jar JavaAnalysis.jar <source-directory> [output-base-path]");
        System.out.println("  Sequence Diagram: java -jar JavaAnalysis.jar -sequence <source-directory> <class-name> <method-name> [output-base-path]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar JavaAnalysis.jar ./src/main/java");
        System.out.println("  java -jar JavaAnalysis.jar -sequence ./sample Cat play output/cat-play-seq");
    }
}
