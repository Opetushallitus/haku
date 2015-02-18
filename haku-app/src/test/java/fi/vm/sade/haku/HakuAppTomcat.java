package fi.vm.sade.haku;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.slf4j.LoggerFactory;

import fi.vm.sade.integrationtest.tomcat.EmbeddedTomcat;
import fi.vm.sade.integrationtest.tomcat.SharedTomcat;
import fi.vm.sade.integrationtest.util.PortChecker;
import fi.vm.sade.integrationtest.util.ProjectRootFinder;

public class HakuAppTomcat extends EmbeddedTomcat {
    static final String HAKU_MODULE_ROOT = ProjectRootFinder.findProjectRoot() + "/haku-app";
    static final String HAKU_CONTEXT_PATH = "/haku-app";
    static final int DEFAULT_PORT = 9090;

    public final static void main(String... args) throws ServletException, LifecycleException {
        useIntegrationTestSettings();
        new HakuAppTomcat(Integer.parseInt(System.getProperty("haku-app.port", String.valueOf(DEFAULT_PORT)))).start().await();
    }

    public HakuAppTomcat(int port) {
        super(port, HAKU_MODULE_ROOT, HAKU_CONTEXT_PATH);
    }

    public static void startShared() {
        SharedTomcat.start(HAKU_MODULE_ROOT, HAKU_CONTEXT_PATH);
    }

    public static void startForIntegrationTestIfNotRunning() {
        useIntegrationTestSettings();
        if (PortChecker.isFreeLocalPort(DEFAULT_PORT)) {
            new HakuAppTomcat(DEFAULT_PORT).start();
        } else {
            LoggerFactory.getLogger(HakuAppTomcat.class).info("Not starting Tomcat: seems to be running on port " + DEFAULT_PORT);
        }
    }

    private static void useIntegrationTestSettings() {
        System.setProperty("application.system.cache", "false");
        System.setProperty("spring.profiles.active", "it");
    }
}