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

public final class JabaResourceReader implements JabaResourceHandler {

    private final Logger LOGGER = Logger.getLogger(JabaResourceReader.class);

    private static final JabaResourceReader SINGLETON = new JabaResourceReader();

    private JabaResourceReader() {
    }

    static JabaResourceReader getSingleton() {
        return SINGLETON;
    }

    private final FileLoader fileLoader = new FileLoader();
    private final StreamLoader streamLoader = new StreamLoader();

    private static class FileLoader {
        File getFile(String resource) {
            if (Paths.get(resource).isAbsolute()) {
                return new File(resource);
            } else {
                return getFile(resource, Thread.currentThread().getContextClassLoader());
            }
        }

        File getFile(String resource, ClassLoader classLoader) {
            return new File(Objects.requireNonNull(classLoader.getResource(resource)).getFile());
        }
    }

    private static class StreamLoader {
        InputStream getStream(String resource) throws FileNotFoundException {
            if (Paths.get(resource).isAbsolute()) {
                return new FileInputStream(resource);
            } else {
                return getStream(resource, Thread.currentThread().getContextClassLoader());
            }
        }

        InputStream getStream(String resource, ClassLoader classLoader) {
            return Objects.requireNonNull(classLoader.getResourceAsStream(resource));
        }
    }

    public <T> T xml(String resourcePath, Class<?> type) throws JAXBException, IOException, SAXException {
        return xml(resourcePath, null, type);
    }

    public <T> T xml(String resourcePath, String xsdPath, Class<?> jaxbFactoryOrType) throws IOException, SAXException, JAXBException {
        T resource = null;
        if (new XmlValidation().run(resourcePath, xsdPath)) {
            JAXBContext jaxbContext = JAXBContext.newInstance(jaxbFactoryOrType);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Object unmarshaled = unmarshaller.unmarshal(file(resourcePath));
            resource = (unmarshaled instanceof JAXBElement) ?
                    ((JAXBElement<T>) unmarshaled).getValue()
                    : (T) unmarshaled;
        }
        if (null == resource) {
            throw new NullPointerException("NPE: Cant't read resource " + resourcePath);
        }
        return resource;
    }

    public Map yaml(String resourceName) throws IOException {
        return yaml(resourceName, Map.class);
    }

    public <T> T yaml(String resourceName, Class<T> type) throws IOException {
        return new ObjectMapper(new YAMLFactory()).readValue(stream(resourceName), type);
    }

    public File file(String resourceName) {
        return fileLoader.getFile(resourceName);
    }

    public File file(String resourceName, ClassLoader classLoader) {
        return fileLoader.getFile(resourceName, classLoader);
    }

    public String fileContent(String resourceName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file(resourceName).getAbsolutePath())));
    }

    public String fileContent(String resourceName, ClassLoader classLoader) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file(resourceName, classLoader).getAbsolutePath())));
    }

    public InputStream stream(String resourceName) throws FileNotFoundException {
        return streamLoader.getStream(resourceName);
    }

    public InputStream stream(String resourceName, ClassLoader classLoader) {
        return streamLoader.getStream(resourceName, classLoader);
    }

    public String streamContent(String resourceName) throws FileNotFoundException {
        return new BufferedReader(
                new InputStreamReader(stream(resourceName), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    public String streamContent(String resourceName, ClassLoader classLoader) {
        return new BufferedReader(
                new InputStreamReader(stream(resourceName, classLoader), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
