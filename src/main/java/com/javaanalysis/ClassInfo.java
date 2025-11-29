package com.javaanalysis;

import java.util.*;

/**
 * Represents information about a Java class
 */
public class ClassInfo {
    private String className;
    private String packageName;
    private boolean isInterface;
    private boolean isAbstract;
    private boolean isEnum;
    private String superClass;
    private List<String> interfaces;
    private List<FieldInfo> fields;
    private List<MethodInfo> methods;
    private List<String> dependencies;

    public ClassInfo(String className, String packageName) {
        this.className = className;
        this.packageName = packageName;
        this.interfaces = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.dependencies = new ArrayList<>();
    }

    // Getters and Setters
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getFullName() {
        return packageName != null && !packageName.isEmpty()
            ? packageName + "." + className
            : className;
    }

    public boolean isInterface() { return isInterface; }
    public void setInterface(boolean isInterface) { this.isInterface = isInterface; }

    public boolean isAbstract() { return isAbstract; }
    public void setAbstract(boolean isAbstract) { this.isAbstract = isAbstract; }

    public boolean isEnum() { return isEnum; }
    public void setEnum(boolean isEnum) { this.isEnum = isEnum; }

    public String getSuperClass() { return superClass; }
    public void setSuperClass(String superClass) { this.superClass = superClass; }

    public List<String> getInterfaces() { return interfaces; }
    public void addInterface(String interfaceName) { this.interfaces.add(interfaceName); }

    public List<FieldInfo> getFields() { return fields; }
    public void addField(FieldInfo field) { this.fields.add(field); }

    public List<MethodInfo> getMethods() { return methods; }
    public void addMethod(MethodInfo method) { this.methods.add(method); }

    public List<String> getDependencies() { return dependencies; }
    public void addDependency(String dependency) {
        if (!dependencies.contains(dependency)) {
            this.dependencies.add(dependency);
        }
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "className='" + className + '\'' +
                ", packageName='" + packageName + '\'' +
                ", isInterface=" + isInterface +
                ", isAbstract=" + isAbstract +
                '}';
    }
}
