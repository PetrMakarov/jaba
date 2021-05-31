package com.pmakarov.jabavalidation;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Validation of xml against xsd scheme
 */
public class XmlValidation {

    public boolean run(String xmlPath, String xsdPath) throws SAXException, IOException {
        if (null == xsdPath || xsdPath.isEmpty()) {
            return true;
        }
        InputStream xsd = getClass().getClassLoader().getResourceAsStream(xsdPath);
        InputStream xml = getClass().getClassLoader().getResourceAsStream(xmlPath);
        return validateAgainstXSD(xml, xsd);
    }

    private boolean validateAgainstXSD(InputStream xml, InputStream xsd) throws SAXException, IOException {
        SchemaFactory factory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new StreamSource(xsd));
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xml));
        return true;
    }
}
