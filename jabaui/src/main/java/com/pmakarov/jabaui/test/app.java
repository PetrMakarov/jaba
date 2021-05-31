package com.pmakarov.jabaui.test;


import com.pmakarov.jabaui.boot.JabaUI;
import com.pmakarov.jabaui.boot.properties.JabaProperties;
import com.pmakarov.jabaui.boot.scope.proxy.compose.JabaControllerProxyComposer;
import com.pmakarov.jabaui.boot.scope.proxy.compose.JabaModelProxyComposer;
import com.pmakarov.jabaui.boot.scope.proxy.exception.JabaProxyCreationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author pmakarov
 */
public class app {
    public static void main(String[] args) throws Exception {
//        Book book = new Book();
//        book.setAuthor("Me");
//        Class<?> bookProxy = new JabaModelProxyComposer().createProxy(book.getClass());
//        Book bookProxyInstance = (Book) bookProxy.getDeclaredConstructor(Book.class).newInstance(book);
//
//        Class<?> panel = new JabaControllerProxyComposer().createProxy(MainPanel.class);
//        Constructor<?> panelConstructor = panel.getConstructor(Book.class);
//        MainPanel panelProxy = (MainPanel) panelConstructor.newInstance(bookProxyInstance);
//        JabaReflection.setFieldValue(panel, panel.getClass().getDeclaredField("book"), bookProxyInstance);
//        Method buildPanel = panel.getClass().getDeclaredMethod("buildPanel");
//        buildPanel.invoke(panel);

//        Class<?> window = new JabaControllerProxyComposer().createProxy(MainWindow.class);
//        Constructor<?> windowConstructor = window.getConstructor(MainPanel.class);
//        MainWindow mainWindow = (MainWindow) windowConstructor.newInstance(panelProxy);
//        JabaReflection.setFieldValue(mainWindow, mainWindow.getClass().getDeclaredField("mainPanel"), panel);
//        Method buildWindow = mainWindow.getClass().getDeclaredMethod("buildWindow");
//        buildWindow.invoke(mainWindow);
//        System.out.println(bookProxyInstance.getAuthor());
        JabaUI.boot(app.class);
//        test3();
    }

    private static void test3() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> constructor = TestSubPanel.class.getDeclaredConstructor(int.class);
        constructor.newInstance(1);
    }

    private static void test2() throws JabaProxyCreationException {
        JabaControllerProxyComposer proxyBuilder = new JabaControllerProxyComposer();
        Object proxy = proxyBuilder.createProxy(MainWindow.class);
        System.out.println();
    }

    private static void test1() {
        String testProp = "${test.hello} ${test.world} test!";
        System.out.println(JabaProperties.injectTo(testProp));
    }

    private static void test0() throws JabaProxyCreationException {
        JabaModelProxyComposer proxyBuilder = new JabaModelProxyComposer();
        Local local = new Local();
        local.setForwardPort(22);
        Remote remote = new Remote();
        remote.setHost("localhost");
        remote.setPort(111);
        Server server = new Server();
        server.setHost("localhost");
        server.setPort(22);
        server.setLogin("login");
        server.setPassword("password");
        Ssh ssh = new Ssh();
        ssh.setName("ssh");
        ssh.setLocal(local);
        ssh.setRemote(remote);
        ssh.setServer(server);
//        Object object = proxyBuilder.createProxy(ssh);
//        ((ProxyBinding<?>) object).getValuePropagator().registerPropagation("name", System.out::println);
//        ((Ssh) object).setName("УРААА");
    }
}
