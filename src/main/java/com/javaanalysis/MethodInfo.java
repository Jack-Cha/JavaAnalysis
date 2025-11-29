package com.javaanalysis;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents information about a method in a Java class
 */
public class MethodInfo {
    private String name;
    private String returnType;
    private String visibility;
    private boolean isStatic;
    private boolean isAbstract;
    private List<ParameterInfo> parameters;

    public MethodInfo(String name, String returnType, String visibility) {
        this.name = name;
        this.returnType = returnType;
        this.visibility = visibility;
        this.parameters = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getReturnType() { return returnType; }
    public void setReturnType(String returnType) { this.returnType = returnType; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public boolean isStatic() { return isStatic; }
    public void setStatic(boolean isStatic) { this.isStatic = isStatic; }

    public boolean isAbstract() { return isAbstract; }
    public void setAbstract(boolean isAbstract) { this.isAbstract = isAbstract; }

    public List<ParameterInfo> getParameters() { return parameters; }
    public void addParameter(ParameterInfo parameter) { this.parameters.add(parameter); }

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
        StringBuilder sb = new StringBuilder();
        sb.append(getUmlVisibility());
        sb.append(name).append("(");

        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(parameters.get(i));
        }

        sb.append(")");
        if (returnType != null && !returnType.isEmpty() && !returnType.equals("void")) {
            sb.append(": ").append(returnType);
        }

        return sb.toString();
    }
}
