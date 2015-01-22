package fi.vm.sade.haku;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;

import fi.vm.sade.integrationtest.tomcat.EmbeddedTomcat;
import fi.vm.sade.integrationtest.tomcat.SharedTomcat;
import fi.vm.sade.integrationtest.util.ProjectRootFinder;

public class HakuAppTomcat extends EmbeddedTomcat {
    static final String HAKU_MODULE_ROOT = ProjectRootFinder.findProjectRoot() + "/haku-app";
    static final String HAKU_CONTEXT_PATH = "haku-app";

    public final static void main(String... args) throws ServletException, LifecycleException {
        new HakuAppTomcat(Integer.parseInt(System.getProperty("haku-app.port", "8091"))).start().await();
    }

    public HakuAppTomcat(int port) {
        super(port, HAKU_MODULE_ROOT, HAKU_CONTEXT_PATH);
    }

    public static void startShared() {
        SharedTomcat.start(HAKU_MODULE_ROOT, HAKU_CONTEXT_PATH);
    }
}