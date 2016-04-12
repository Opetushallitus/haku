package fi.vm.sade.haku;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.slf4j.LoggerFactory;

import fi.vm.sade.integrationtest.tomcat.EmbeddedTomcat;
import fi.vm.sade.integrationtest.util.PortChecker;
import fi.vm.sade.integrationtest.util.ProjectRootFinder;

import java.net.URL;
import java.net.URLClassLoader;

public class HakuAppTomcat extends EmbeddedTomcat {
    static final String HAKU_MODULE_ROOT = ProjectRootFinder.findProjectRoot() + "/haku-app";
    static final String HAKU_CONTEXT_PATH = "/haku-app";
    static final int DEFAULT_PORT = 9090;
    static final int DEFAULT_AJP_PORT = 8506;

    public final static void main(String... args) throws ServletException, LifecycleException {
        useIntegrationTestSettingsIfNoProfileSelected();
        new HakuAppTomcat(Integer.parseInt(System.getProperty("haku-app.port", String.valueOf(DEFAULT_PORT))),
                Integer.parseInt(System.getProperty("haku-app.port.ajp", String.valueOf(DEFAULT_AJP_PORT)))
        ).start().await();
    }

    public HakuAppTomcat(int port, int ajpPort) {
        super(port, ajpPort, HAKU_MODULE_ROOT, HAKU_CONTEXT_PATH);
        addWebApp(ProjectRootFinder.findProjectRoot() + "/haku-mock", "/");
        printClassPath();
    }

    private static void printClassPath() {
        System.out.println("CLASSPATH POW");
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader)cl).getURLs();
        for(URL url: urls){
            System.out.println(url.getFile());
        }
    }

    public static void startForIntegrationTestIfNotRunning() {
        useIntegrationTestSettingsIfNoProfileSelected();
        if (PortChecker.isFreeLocalPort(DEFAULT_PORT) && PortChecker.isFreeLocalPort(DEFAULT_AJP_PORT)) {
            new HakuAppTomcat(DEFAULT_PORT, DEFAULT_AJP_PORT).start();
        } else {
            LoggerFactory.getLogger(HakuAppTomcat.class).info("Not starting Tomcat: seems to be running on ports " + DEFAULT_PORT + "," + DEFAULT_AJP_PORT);
        }
    }

    private static void useIntegrationTestSettingsIfNoProfileSelected() {
        //System.setProperty("application.system.cache", "false");
        if (System.getProperty("spring.profiles.active") == null) {
            System.setProperty("spring.profiles.active", "it");
        }
        System.out.println("Running embedded with profile " + System.getProperty("spring.profiles.active"));
    }
}