package com.javaanalysis;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequencePlantUMLGenerator {
    private static final Logger logger = LoggerFactory.getLogger(SequencePlantUMLGenerator.class);

    public void generateDiagram(String startClass, String startMethod, List<SequenceAnalyzer.CallTrace> traces, String outputBasePath) throws IOException {
        StringBuilder uml = new StringBuilder();
        uml.append("@startuml\n");
        uml.append("skinparam style strictuml\n"); // Cleaner look
        uml.append("actor User\n");
        
        uml.append("User -> ").append(startClass).append(" : ").append(startMethod).append("()\n");
        uml.append("activate ").append(startClass).append("\n");

        for (SequenceAnalyzer.CallTrace trace : traces) {
            String source = trace.sourceClass;
            String target = trace.targetClass;
            
            // Skip self-calls if desired, or represent them
            if (target.equals("Unknown")) target = trace.methodName + "_Target"; // Fallback

            uml.append(source).append(" -> ").append(target).append(" : ").append(trace.methodName).append("()\n");
            uml.append("activate ").append(target).append("\n");
            // Typically in a simple trace we might assume immediate return for visualization
            // or we just show the call flow.
            uml.append(target).append(" --> ").append(source).append(" : ").append(getSimpleType(trace.returnType)).append("\n");
            uml.append("deactivate ").append(target).append("\n");
        }

        uml.append("deactivate ").append(startClass).append("\n");
        uml.append("@enduml\n");

        String plantUMLCode = uml.toString();
        
        // Save .puml
        Path pumlPath = Paths.get(outputBasePath + ".puml");
        try (BufferedWriter writer = Files.newBufferedWriter(pumlPath, StandardCharsets.UTF_8)) {
            writer.write(plantUMLCode);
        }

        // Generate PNG
        try (FileOutputStream outputStream = new FileOutputStream(outputBasePath + ".png")) {
            SourceStringReader reader = new SourceStringReader(plantUMLCode);
            reader.outputImage(outputStream, new FileFormatOption(FileFormat.PNG));
        }
        
        logger.info("Sequence diagram generated at: {}", outputBasePath);
    }

    private String getSimpleType(String type) {
        if (type == null) return "";
        int lastDot = type.lastIndexOf('.');
        if (lastDot > 0) return type.substring(lastDot + 1);
        return type;
    }
}
