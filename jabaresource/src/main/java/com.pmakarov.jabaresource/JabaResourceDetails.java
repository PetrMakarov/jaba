package com.pmakarov.jabaresource;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.xml.namespace.QName;

/**
 * @author pmakarov
 */
@Data
@AllArgsConstructor
public class JabaResourceDetails {
    private Class<?> jaxbFactory;
    private QName qName;
    //TODO: separate by type
    private String output;
    private String xsd;
}
