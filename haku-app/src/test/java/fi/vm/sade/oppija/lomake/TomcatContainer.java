/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.lomake;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.DisposableBean;

import java.io.File;
import java.io.IOException;

/**
 * @author jukka
 * @version 10/19/122:03 PM}
 * @since 1.1
 */
public class TomcatContainer implements DisposableBean {

    public static final String HAKU_APP = "";//"haku-app/";
    public static final String SPRING_PROFILES_ACTIVE_KEY = "spring.profiles.active";
    public static final String SPRING_PROFILES_ACTIVE_VALUE_DEV = "dev";

    /**
     * The temporary directory in which Tomcat and the app are deployed.
     */
    private static final String M_WORKING_DIR = HAKU_APP + "target/it";
    public static final String MONGO_DB_NAME = "mongo.db.name";
    public static final String APPLICATION_ID = "haku-app";
    /**
     * The tomcat instance.
     */
    private Tomcat mTomcat;
    private int port;

    private final String name;

    public TomcatContainer(final String name) throws LifecycleException, IOException {
        this.name = name;
        createTomcat();
        mTomcat.start();
        port = mTomcat.getConnector().getLocalPort();
        createWebApp(new File("target/haku-app.war"));
    }

    @Override
    public void destroy() throws Exception {
        try {
            if (mTomcat.getServer() != null
                    && mTomcat.getServer().getState() != LifecycleState.DESTROYED) {
                if (mTomcat.getServer().getState() != LifecycleState.STOPPED) {
                    mTomcat.stop();
                }
            }
        } finally {
            mTomcat.destroy();
        }
    }


    private void createTomcat() {
        mTomcat = new Tomcat();
        mTomcat.setPort(0);
        mTomcat.setBaseDir(M_WORKING_DIR);
        mTomcat.getHost().setAppBase(M_WORKING_DIR);
        mTomcat.getHost().setAutoDeploy(false);
        mTomcat.getHost().setDeployOnStartup(false);
    }


    private void createWebApp(File webApp) throws IOException {
        System.setProperty(MONGO_DB_NAME, name);
        System.setProperty(SPRING_PROFILES_ACTIVE_KEY, SPRING_PROFILES_ACTIVE_VALUE_DEV);
        mTomcat.addWebapp(mTomcat.getHost(), getContextPath(), webApp.getAbsolutePath());

    }

    public String getContextPath() {
        return "/" + APPLICATION_ID;
    }

    public String getBaseUrl() {
        return "http://localhost:" + getPort() + getContextPath() + "/";
    }

    public int getPort() {
        return port;
    }
}
