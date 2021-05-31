package com.pmakarov.jabaresource;

import com.pmakarov.jabavalidation.XmlValidation;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;

class JabaResourceWriter implements JabaResourceHandler {

    private final Logger LOGGER = Logger.getLogger(JabaResourceWriter.class);

    private static final JabaResourceWriter SINGLETON = new JabaResourceWriter();

    private JabaResourceWriter() {
    }

    static JabaResourceWriter getSingleton() {
        return SINGLETON;
    }

    public <T> void xml(final String resourcePath, final T object) throws JAXBException, IOException, SAXException {
        xml(resourcePath, null, object);
    }

    public <T> void xml(final String resourcePath, final String xsdPath, final T object) throws JAXBException, IOException, SAXException {
        if (new XmlValidation().run(resourcePath, xsdPath)) {
            JAXBContext jc = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(object, new File(resourcePath));
        }
    }
}
