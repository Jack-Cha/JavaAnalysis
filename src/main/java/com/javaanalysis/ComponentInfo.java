package com.javaanalysis;

import java.util.*;

/**
 * Represents information about a component (package) in the system
 */
public class ComponentInfo {
    private String componentName;
    private Set<String> classes;
    private Set<String> interfaces;
    private Set<String> dependencies;
    private Set<String> providedInterfaces;
    private Set<String> requiredInterfaces;

    public ComponentInfo(String componentName) {
        this.componentName = componentName;
        this.classes = new HashSet<>();
        this.interfaces = new HashSet<>();
        this.dependencies = new HashSet<>();
        this.providedInterfaces = new HashSet<>();
        this.requiredInterfaces = new HashSet<>();
    }

    // Getters
    public String getComponentName() {
        return componentName;
    }

    public Set<String> getClasses() {
        return classes;
    }

    public Set<String> getInterfaces() {
        return interfaces;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public Set<String> getProvidedInterfaces() {
        return providedInterfaces;
    }

    public Set<String> getRequiredInterfaces() {
        return requiredInterfaces;
    }

    // Add methods
    public void addClass(String className) {
        this.classes.add(className);
    }

    public void addInterface(String interfaceName) {
        this.interfaces.add(interfaceName);
    }

    public void addDependency(String dependency) {
        this.dependencies.add(dependency);
    }

    public void addProvidedInterface(String interfaceName) {
        this.providedInterfaces.add(interfaceName);
    }

    public void addRequiredInterface(String interfaceName) {
        this.requiredInterfaces.add(interfaceName);
    }

    @Override
    public String toString() {
        return "ComponentInfo{" +
                "componentName='" + componentName + '\'' +
                ", classes=" + classes.size() +
                ", interfaces=" + interfaces.size() +
                ", dependencies=" + dependencies.size() +
                '}';
    }
}
