package com.pmakarov.jabaui.style.apply;

import com.pmakarov.jabahelper.JabaReflection;
import com.pmakarov.jabaresource.JabaResource;
import com.pmakarov.jabaui.style.parse.exception.JabaStyleException;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.pmakarov.jabaui.style.objects.JCSSStyle.PSEUDO_PREFIX;

final class ApplyersAccessor {

    /**
     * Singleton object
     */
    private static final ApplyersAccessor SINGLETON = new ApplyersAccessor();

    /**
     * List of style applyers
     */
    private static final List<StyleApplyer<Component>> APPLYERS = loadApplayersDescription();

    private ApplyersAccessor() {
    }

    /**
     * Load all available style applyers from description file
     *
     * @return style applyers object list
     */
    private static List<StyleApplyer<Component>> loadApplayersDescription() {
        try {
            List<StyleApplyer<Component>> applyersDescription = new ArrayList<>();
            //load file
            Map applyersDescriptionContent = JabaResource.getReader().yaml("applyers.description.yaml");
            //iterate for content
            Iterator entries = applyersDescriptionContent.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                //get style applyer class name and inspects class
                String styleApplyerName = (String) entry.getKey();
                StyleApplyer<Component> styleApplyer = (StyleApplyer<Component>) JabaReflection.createNewInstance(Class.forName(styleApplyerName));
                //iterate content
                Map body = (Map) entry.getValue();
                if (null != body) {
                    Iterator bodyEntries = body.entrySet().iterator();
                    while (bodyEntries.hasNext()) {
                        Map.Entry bodyEntry = (Map.Entry) bodyEntries.next();
                        // get key (property or pseudo)
                        String bodyKey = (String) bodyEntry.getKey();
                        // if property
                        if (!bodyKey.startsWith(PSEUDO_PREFIX)) {
                            //get property name
                            String bodyValue = (String) bodyEntry.getValue();
                            //get class and put in properties
                            styleApplyer.getAvailableProperties().putIfAbsent(bodyKey, (PropertyApplyer<Component>) JabaReflection.createNewInstance(Class.forName(bodyValue)));
                        } else {
                            //if pseudo
                            Map pseudo = (Map) bodyEntry.getValue();
                            Iterator pseudoIterator = pseudo.entrySet().iterator();
                            while (pseudoIterator.hasNext()) {
                                Map.Entry pseudoEntry = (Map.Entry) pseudoIterator.next();
                                // get applyer name and class
                                String pseudoApplyerName = (String) pseudoEntry.getKey();
                                PseudoApplyer<Component> pseudoApplyer = (PseudoApplyer<Component>) JabaReflection.createNewInstance(Class.forName(pseudoApplyerName));
                                // iterate over pseudo applyer
                                Map pseudoBody = (Map) pseudoEntry.getValue();
                                Iterator pseudoBodyIterator = pseudoBody.entrySet().iterator();
                                while (pseudoBodyIterator.hasNext()) {
                                    Map.Entry pseudoBodyEntry = (Map.Entry) pseudoBodyIterator.next();
                                    //pseudo property
                                    String pseudoProperty = (String) pseudoBodyEntry.getKey();
                                    //pseudo applyer name and class add
                                    String pseudoPropertyApplyerName = (String) pseudoBodyEntry.getValue();
                                    pseudoApplyer.getAvailableProperties().putIfAbsent(pseudoProperty, (PropertyApplyer<Component>) JabaReflection.createNewInstance(Class.forName(pseudoPropertyApplyerName)));
                                }
                                styleApplyer.getAvailablePseudos().putIfAbsent(bodyKey, pseudoApplyer);
                            }
                        }
                    }
                }
                applyersDescription.add(styleApplyer);
            }
            return applyersDescription;
        } catch (IOException | ClassNotFoundException e) {
            throw new JabaStyleException("Error load style applyers", e);
        }
    }

    public static ApplyersAccessor getSingleton() {
        return SINGLETON;
    }

    public List<StyleApplyer<Component>> getList() {
        return APPLYERS;
    }
}
