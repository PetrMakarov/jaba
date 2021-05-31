package com.pmakarov.jabaui.boot;

import com.pmakarov.jabaui.boot.locator.SourceLocatorFactory;
import com.pmakarov.jabaui.boot.scope.exception.JabaDependencyInjectException;
import com.pmakarov.jabaui.boot.scope.exception.JabaScopeInitException;
import com.pmakarov.jabaui.style.apply.JCSSMapper;

import java.awt.*;
import java.util.Set;

/**
 * Class for boot <code>Jaba application</code>
 *
 * @author pmakarov
 */
public final class JabaUI {

    private JabaUI() {
    }

    private static JCSSMapper jcssMapper;

    /**
     * Boot point (startup) method
     *
     * @param bootClass root class of application
     */
    public static void boot(Class<?> bootClass) {
        try {
            jcssMapper = new JCSSMapper();
            BootSource bootSource = new BootSource(bootClass);
            Set<Class<?>> locatedSource = SourceLocatorFactory.create(bootSource.getType()).locate(bootSource.getSource());
            new CDIService(locatedSource).initAndInjectDependencies();
        } catch (JabaDependencyInjectException e) {
            throw new JabaBootException("Jaba ui boot error", e);
        }
    }

    /**
     * Open window with applying styles for it
     *
     * @param window passed window
     */
    public static void open(Window window) {
        try {
            window.setVisible(false);
            jcssMapper.applyStyles(window);
            window.pack();
            window.setVisible(true);
        } catch (Exception e) {
            throw new JabaBootException("Jaba ui boot error", e);
        }
    }
}
