package fi.vm.sade.haku;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;

import fi.vm.sade.integrationtest.tomcat.EmbeddedTomcat;
import fi.vm.sade.integrationtest.util.ProjectRootFinder;

public class HakuAppTomcat {
    static final String HAKU_MODULE_ROOT = ProjectRootFinder.findProjectRoot() + "/haku-app";
    static final String HAKU_CONTEXT_PATH = "/haku-app";
    static final int DEFAULT_PORT = 9090;
    static final int DEFAULT_AJP_PORT = 8506;
    private static EmbeddedTomcat tomcat = null;

    public final static void main(String... args) throws ServletException, LifecycleException {
        useIntegrationTestSettingsIfNoProfileSelected();
        create(Integer.parseInt(System.getProperty("haku-app.port", String.valueOf(DEFAULT_PORT))),
                Integer.parseInt(System.getProperty("haku-app.port.ajp", String.valueOf(DEFAULT_AJP_PORT)))
        ).start().await();
    }

    public static EmbeddedTomcat create(int port, int ajpPort) {
        useIntegrationTestSettingsIfNoProfileSelected();
        return new EmbeddedTomcat(port, ajpPort, HAKU_MODULE_ROOT, HAKU_CONTEXT_PATH).
                addWebApp(ProjectRootFinder.findProjectRoot() + "/haku-mock", "/");
    }

    synchronized public static void startForIntegrationTestIfNotRunning() {
        if(tomcat == null) {
            tomcat = create(DEFAULT_PORT, DEFAULT_AJP_PORT);
            tomcat.start();
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