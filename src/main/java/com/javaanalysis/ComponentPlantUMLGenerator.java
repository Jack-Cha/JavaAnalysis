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
 * Generates PlantUML component diagrams from component information
 */
public class ComponentPlantUMLGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ComponentPlantUMLGenerator.class);

    static {
        // Force UTF-8 encoding for PlantUML to avoid CP949 issues on Windows
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("plantuml.charset", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        System.setProperty("PLANTUML_SECURITY_PROFILE", "UNSECURE");
    }

    /**
     * Generates PlantUML component diagram code
     *
     * @param componentMap Map of component information
     * @return PlantUML code as string
     */
    public String generatePlantUML(Map<String, ComponentInfo> componentMap) {
        StringBuilder uml = new StringBuilder();

        uml.append("@startuml\n");
        uml.append("skinparam componentStyle rectangle\n");
        uml.append("skinparam component {\n");
        uml.append("  BackgroundColor<<component>> LightBlue\n");
        uml.append("  BorderColor DarkBlue\n");
        uml.append("  FontSize 12\n");
        uml.append("}\n");
        uml.append("left to right direction\n\n");

        // Generate component definitions
        for (ComponentInfo component : componentMap.values()) {
            generateComponentDefinition(component, uml);
            uml.append("\n");
        }

        // Generate relationships (dependencies)
        uml.append("\n' Component Dependencies\n");
        generateDependencies(componentMap, uml);

        uml.append("@enduml\n");

        return uml.toString();
    }

    /**
     * Generates a single component definition
     */
    private void generateComponentDefinition(ComponentInfo component, StringBuilder uml) {
        String componentName = component.getComponentName();
        String safeComponentName = sanitizeComponentName(componentName);

        uml.append("component \"").append(componentName).append("\" as ").append(safeComponentName);
        uml.append(" <<component>> {\n");

        // List provided interfaces
        if (!component.getProvidedInterfaces().isEmpty()) {
            uml.append("  [Provided Interfaces]\n");
            for (String interfaceName : component.getProvidedInterfaces()) {
                uml.append("  interface ").append(interfaceName).append("\n");
            }
            uml.append("\n");
        }

        // List classes
        if (!component.getClasses().isEmpty()) {
            uml.append("  [Classes: ").append(component.getClasses().size()).append("]\n");
        }

        uml.append("}\n");

        // Generate provided interface ports
        for (String interfaceName : component.getProvidedInterfaces()) {
            uml.append(safeComponentName).append(" -up- () ").append(interfaceName).append("\n");
        }
    }

    /**
     * Generates dependency relationships between components
     */
    private void generateDependencies(Map<String, ComponentInfo> componentMap, StringBuilder uml) {
        for (ComponentInfo component : componentMap.values()) {
            String sourceComponentName = sanitizeComponentName(component.getComponentName());

            for (String targetPackage : component.getDependencies()) {
                String targetComponentName = sanitizeComponentName(targetPackage);

                // Check if target component exists
                if (componentMap.containsKey(targetPackage)) {
                    uml.append(sourceComponentName).append(" ..> ").append(targetComponentName)
                            .append(" : uses\n");
                }
            }
        }
    }

    /**
     * Sanitizes component name to be valid PlantUML identifier
     */
    private String sanitizeComponentName(String name) {
        return name.replace(".", "_").replace("(", "").replace(")", "");
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
            SourceStringReader reader = new SourceStringReader(plantUMLCode);
            DiagramDescription description = reader.outputImage(outputStream, new FileFormatOption(format));

            if (description != null) {
                String desc = description.getDescription();
                logger.debug("PlantUML generation result: {}", desc);

                if (desc != null && (desc.toLowerCase().contains("error") || desc.toLowerCase().contains("syntax"))) {
                    logger.error("PlantUML generation error: {}", desc);
                    throw new IOException("PlantUML failed to generate image: " + desc);
                }
            }
        }

        logger.info("Component diagram image saved to: {}", outputPath);
    }

    /**
     * Generates both PlantUML file and images (PNG, SVG)
     */
    public void generateDiagram(Map<String, ComponentInfo> componentMap, String basePath) throws IOException {
        String plantUMLCode = generatePlantUML(componentMap);

        // Save PlantUML source
        String pumlPath = basePath + ".puml";
        savePlantUMLFile(plantUMLCode, pumlPath);

        // Generate PNG image
        String pngPath = basePath + ".png";
        generateImage(plantUMLCode, pngPath, FileFormat.PNG);

        // Generate SVG image (optional, better quality)
        String svgPath = basePath + ".svg";
        generateImage(plantUMLCode, svgPath, FileFormat.SVG);

        logger.info("Component diagrams generated successfully");
    }
}
