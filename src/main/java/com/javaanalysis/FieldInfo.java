package com.javaanalysis;

/**
 * Represents information about a field in a Java class
 */
public class FieldInfo {
    private String name;
    private String type;
    private String visibility; // public, private, protected, package-private

    public FieldInfo(String name, String type, String visibility) {
        this.name = name;
        this.type = type;
        this.visibility = visibility;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public String getUmlVisibility() {
        switch (visibility) {
            case "public": return "+";
            case "private": return "-";
            case "protected": return "#";
            default: return "~"; // package-private
        }
    }

    @Override
    public String toString() {
        return getUmlVisibility() + name + ": " + type;
    }
}
