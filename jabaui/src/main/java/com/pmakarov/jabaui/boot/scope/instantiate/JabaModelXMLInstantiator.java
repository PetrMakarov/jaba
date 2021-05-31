package com.pmakarov.jabaui.boot.scope.instantiate;

import com.pmakarov.jabaresource.JabaResource;
import com.pmakarov.jabaui.boot.JabaBootException;
import com.pmakarov.jabaui.boot.metadata.JabaModelXml;
import com.pmakarov.jabaui.boot.properties.JabaProperties;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Class represents factory for application xml model instances creation
 */
class JabaModelXMLInstantiator extends JabaCDInstantiator {

    public JabaModelXMLInstantiator() {
        super();
    }

    @Override
    boolean isSuitableFor(Class<?> locatedClass) {
        return locatedClass.isAnnotationPresent(JabaModelXml.class);
    }

    @Override
    Object instantiate(Class<?> modelClass, Class<?> proxy) {
        try {
            JabaModelXml modelDescription = modelClass.getAnnotation(JabaModelXml.class);
            String resource = JabaProperties.injectTo(modelDescription.resource());
            String validate = JabaProperties.injectTo(modelDescription.validate());
            Class<?> type = void.class.equals(modelDescription.jaxbFactory()) ? modelClass : modelDescription.jaxbFactory();
            Object instance = JabaResource.getReader().xml(resource, validate, type);
            instance = proxy.getConstructor(instance.getClass()).newInstance(instance);
            return instance;
        } catch (IOException | SAXException | JAXBException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new JabaBootException("Error create model " + modelClass.getName(), e);
        }
    }
}
