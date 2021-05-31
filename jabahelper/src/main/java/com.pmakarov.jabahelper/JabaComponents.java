package com.pmakarov.jabahelper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author pmakarov
 */
public final class JabaComponents {
    private JabaComponents() {
    }

    /**
     * Get root window
     *
     * @param current current component
     * @return root window frame
     */
    public static JFrame getRootFrame(Component current) {
        return (JFrame) SwingUtilities.windowForComponent(current);
    }

    /**
     * Find component by class in whole app
     *
     * @param current current component
     * @param calzz   class of component
     * @return found component
     */
    public static <T> T findInAppTree(Component current, Class<T> calzz) {
        return findInContainer(getRootFrame(current), calzz);
    }

    /**
     * Find from current component cascade
     *
     * @param current current component
     * @param clazz   component class
     * @return found component
     */
    public static <T> T findInContainer(Component current, Class<T> clazz) {
        T result;
        if (clazz.equals(current.getClass())) {
            return (T) current;
        } else {
            if (current instanceof Container) {
                Container c = (Container) current;
                for (int i = 0; i < c.getComponents().length; i++) {
                    result = findInContainer(c.getComponent(i), clazz);
                    if (result != null) {
                        return (T) result;
                    }
                }
            }
        }
        return null;
    }

    public static List<Component> getChilds(Component component) {
        if (component instanceof Container) {
            return Arrays.asList(((Container) component).getComponents());
        } else {
            return new ArrayList<>();
        }
    }

    public static void performDeep(Component current, ComponentAction action) {
        action.perform(current);
        if (current instanceof Container) {
            Container c = (Container) current;
            for (int i = 0; i < c.getComponents().length; i++) {
                performDeep(c.getComponent(i), action);
            }
        }
    }

    /**
     * Execute action for all components match given predicate nested in given container
     *
     * @param current   container
     * @param predicate predicate
     * @param action    action
     */
    public static void performDeep(Component current, Predicate<Component> predicate, ComponentAction action) {
        if (predicate.test(current)) {
            action.perform(current);
        } else {
            if (current instanceof Container) {
                Container c = (Container) current;
                for (int i = 0; i < c.getComponents().length; i++) {
                    performDeep(c.getComponent(i), predicate, action);
                }
            }
        }
    }

    /**
     * Execute action for all components match given predicate in whole app
     *
     * @param current   container
     * @param predicate predicate
     * @param action    action
     */
    public static void performDeepFromRoot(Component current, Predicate<Component> predicate, ComponentAction action) {
        performDeep(getRootFrame(current), predicate, action);
    }

    /**
     * Execute action for all components with given class nested in given container
     *
     * @param current container
     * @param clazz   class of component
     * @param action  action
     */
    public static void performDeepByClass(Component current, Class<?> clazz, ComponentAction action) {
        Predicate<Component> predicate = component -> clazz.equals(component.getClass());
        performDeep(current, predicate, action);
    }

    /**
     * Execute action for all components with given class in whole app
     *
     * @param current container
     * @param clazz   class of component
     * @param action  action
     */
    public static void performDeepByClassGlobal(Component current, Class<?> clazz, ComponentAction action) {
        performDeepByClass(getRootFrame(current), clazz, action);
    }

    /**
     * Action for component
     */
    public interface ComponentAction {
        /**
         * Method that will be invoked for component
         *
         * @param component component instance
         */
        void perform(Component component);
    }
}
