package com.pmakarov.jabaproxygen;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author pmakarov
 * Class represent object to generate proxy file content
 */
@Data
@NoArgsConstructor
public class JabaGeneratedClassEntry {
    private String packageString;// output package
    private List<String> imports;// source imports
    private List<String> annotations;// source annotations
    private String name;// generated name
    private String extendsClassName;// source class name
    private String extendsInstanceName;// source class instance name
    private String implementsClassName = "com.pmakarov.jabaui.bindings.ProxyBinding";
    private String propagatorClassName = "com.pmakarov.jabaui.bindings.ValuePropagator";
    private String propagatorInstanceName = "valuePropagator";
    private String proxyWrapeeString;
    private List<JabaGeneratedClassMethodEntry> methods;// source methods
}
