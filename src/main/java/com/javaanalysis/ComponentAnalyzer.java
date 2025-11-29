package com.javaanalysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Analyzes components (packages) and their dependencies from class information
 */
public class ComponentAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(ComponentAnalyzer.class);

    /**
     * Analyzes class information and groups them into components (packages)
     *
     * @param classInfoMap Map of class information
     * @return Map of component name to ComponentInfo
     */
    public Map<String, ComponentInfo> analyzeComponents(Map<String, ClassInfo> classInfoMap) {
        Map<String, ComponentInfo> componentMap = new HashMap<>();

        // Step 1: Group classes by package
        for (ClassInfo classInfo : classInfoMap.values()) {
            String packageName = classInfo.getPackageName();
            if (packageName == null || packageName.isEmpty()) {
                packageName = "(default)";
            }

            ComponentInfo component = componentMap.computeIfAbsent(
                packageName,
                ComponentInfo::new
            );

            // Add class or interface to component
            if (classInfo.isInterface()) {
                component.addInterface(classInfo.getClassName());
                component.addProvidedInterface(classInfo.getClassName());
            } else {
                component.addClass(classInfo.getClassName());
            }
        }

        // Step 2: Analyze dependencies between components
        for (ClassInfo classInfo : classInfoMap.values()) {
            String sourcePackage = classInfo.getPackageName();
            if (sourcePackage == null || sourcePackage.isEmpty()) {
                sourcePackage = "(default)";
            }

            ComponentInfo sourceComponent = componentMap.get(sourcePackage);

            // Analyze dependencies
            for (String dependency : classInfo.getDependencies()) {
                // Find the package of the dependency
                String targetPackage = findPackageForClass(dependency, classInfoMap);

                if (targetPackage != null && !targetPackage.equals(sourcePackage)) {
                    sourceComponent.addDependency(targetPackage);

                    // If the dependency is an interface, mark it as required
                    ClassInfo depClassInfo = findClassInfo(dependency, classInfoMap);
                    if (depClassInfo != null && depClassInfo.isInterface()) {
                        sourceComponent.addRequiredInterface(dependency);
                    }
                }
            }
        }

        logger.info("Found {} components", componentMap.size());
        for (ComponentInfo component : componentMap.values()) {
            logger.info("  - Component: {} ({} classes, {} interfaces, {} dependencies)",
                    component.getComponentName(),
                    component.getClasses().size(),
                    component.getInterfaces().size(),
                    component.getDependencies().size());
        }

        return componentMap;
    }

    /**
     * Finds the package name for a given class name
     */
    private String findPackageForClass(String className, Map<String, ClassInfo> classInfoMap) {
        // Try to find by simple class name
        for (ClassInfo classInfo : classInfoMap.values()) {
            if (classInfo.getClassName().equals(className) ||
                classInfo.getFullName().equals(className)) {
                String packageName = classInfo.getPackageName();
                return (packageName == null || packageName.isEmpty()) ? "(default)" : packageName;
            }
        }
        return null;
    }

    /**
     * Finds ClassInfo for a given class name
     */
    private ClassInfo findClassInfo(String className, Map<String, ClassInfo> classInfoMap) {
        for (ClassInfo classInfo : classInfoMap.values()) {
            if (classInfo.getClassName().equals(className) ||
                classInfo.getFullName().equals(className)) {
                return classInfo;
            }
        }
        return null;
    }
}
