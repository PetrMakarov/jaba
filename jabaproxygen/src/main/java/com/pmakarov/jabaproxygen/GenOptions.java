package com.pmakarov.jabaproxygen;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author pmakarov
 * Options of maven plugin
 */
public class GenOptions {

    @Parameter(property = "sourcePackage", defaultValue = "", required = true)
    private String sourcePackage;
    @Parameter(property = "sourcePackage", defaultValue = "", required = true)
    private String generatedPackage;
    @Parameter(property = "exclude", defaultValue = "")
    private String exclude;

    public GenOptions() {
    }

    public GenOptions(String sourcePackage, String generatedPackage, String exclude) {
        this.sourcePackage = sourcePackage;
        this.generatedPackage = generatedPackage;
        this.exclude = exclude;
    }

    public String getSourcePackage() {
        return sourcePackage;
    }

    public void setSourcePackage(String sourcePackage) {
        this.sourcePackage = sourcePackage;
    }

    public String getGeneratedPackage() {
        return generatedPackage;
    }

    public void setGeneratedPackage(String generatedPackage) {
        this.generatedPackage = generatedPackage;
    }

    public String getExclude() {
        if (null == exclude) {
            exclude = "";
        }
        return exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    @Override
    public String toString() {
        return "source[" + sourcePackage + "] target[" + generatedPackage + "] exclude[" + exclude + "]";
    }
}
