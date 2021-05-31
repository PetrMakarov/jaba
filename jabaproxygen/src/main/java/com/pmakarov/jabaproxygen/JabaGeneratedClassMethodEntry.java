package com.pmakarov.jabaproxygen;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author pmakarov
 * Class represents method of object to generate proxy file content
 */
@Data
@NoArgsConstructor
public class JabaGeneratedClassMethodEntry {
    private String modifiers;// method modifiers
    private String returnType;// method return type
    private String name;// method name
    private String parameters;// method params
    private String body;// method body
    private String declareSignature;// declare signature
    private String callSignature;// call signature
}
