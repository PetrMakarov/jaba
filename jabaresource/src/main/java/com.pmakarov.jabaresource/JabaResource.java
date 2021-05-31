package com.pmakarov.jabaresource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.pmakarov.jabavalidation.XmlValidation;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author pmakarov
 */
public final class JabaResource {

    private final static Logger LOGGER = Logger.getLogger(JabaResource.class);

    private JabaResource() {
    }

    public static JabaResourceReader getReader() {
        return JabaResourceReader.getSingleton();
    }

    public static JabaResourceWriter getWriter() {
        return JabaResourceWriter.getSingleton();
    }
}
