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
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.EffectivePomMavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.springframework.beans.factory.DisposableBean;

import java.io.File;
import java.io.IOException;

/**
 * @author jukka
 * @version 10/19/122:03 PM}
 * @since 1.1
 */
public class TomcatContainer implements DisposableBean {

    private static final String WEBAPP_SRC = "src/main/webapp";
    private static final String RESOURCES_SRC = "src/main/resources";
    public static final String SPRING_PROFILES_ACTIVE_KEY = "spring.profiles.active";
    public static final String SPRING_PROFILES_ACTIVE_VALUE_DEV = "dev";

    /**
     * The temporary directory in which Tomcat and the app are deployed.
     */
    private static final String M_WORKING_DIR = "target/it";
    private static final String SOLR_SOLR_HOME = "solr.solr.home";
    private static final String TARJONTA_INDEX_URL = "tarjonta.index.url";
    private static final String TARJONTA_DATA_URL = "tarjonta.data.url";
    public static final String MONGO_DB_NAME = "mongo.db.name";
    /**
     * The tomcat instance.
     */
    private Tomcat mTomcat;
    private static File solr;
    private int port;

    private final String name;

    public TomcatContainer(final String name) throws LifecycleException, IOException {
        System.setProperty("webdriver.firefox.bin", "/Users/hannu/software/Firefox15.app/Contents/MacOS/firefox");
        this.name = name;
        createTomcat();
        mTomcat.start();
        port = mTomcat.getConnector().getLocalPort();
        createWebApp(createPackage());
    }

    private static WebArchive addWebResourcesTo(WebArchive archive) {
        final File webAppDirectory = new File(WEBAPP_SRC);
        for (File file : org.apache.commons.io.FileUtils.listFiles(webAppDirectory, null, true)) {
            if (isValidFilename(file)) {
                archive.addAsWebResource(file, file.getPath().substring(WEBAPP_SRC.length()));
            }
        }
        return archive;
    }

    private static boolean isValidFilename(File file) {
        return !file.isDirectory() && !file.getName().equals(".svn");
    }

    @Override
    public void destroy() throws Exception {
        System.clearProperty(SOLR_SOLR_HOME);
        System.clearProperty(TARJONTA_INDEX_URL);
        System.clearProperty(TARJONTA_DATA_URL);
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
        mTomcat.getHost().setAutoDeploy(true);
        mTomcat.getHost().setDeployOnStartup(false);
    }


    private void createWebApp(File webApp) throws IOException {
        System.setProperty(SOLR_SOLR_HOME, new File("target/resources/solr").getAbsolutePath());
        System.setProperty(TARJONTA_INDEX_URL, "http://localhost:" + getPort() + "/solr/");
        System.setProperty(TARJONTA_DATA_URL, "http://localhost:" + getPort() + "/haku/tarjontadev/learningDownloadPOC.xml");
        System.setProperty(MONGO_DB_NAME, name);
        System.setProperty(SPRING_PROFILES_ACTIVE_KEY, SPRING_PROFILES_ACTIVE_VALUE_DEV);
        prepareSolr();
        mTomcat.addWebapp(mTomcat.getHost(), "/solr", solr.getAbsolutePath());
        mTomcat.addWebapp(mTomcat.getHost(), getContextPath(), webApp.getAbsolutePath());

    }


    private void prepareSolr() throws IOException {
        final File target = new File("target");
        org.apache.commons.io.FileUtils.copyDirectoryToDirectory(new File(RESOURCES_SRC), target);


        for (File file : org.apache.commons.io.FileUtils.listFiles(target, null, true)) {
            if (file.getName().equals(".svn")) {
                file.delete();
            }
        }
    }

    private static File createPackage() throws IOException {
        final File workDir = new File(M_WORKING_DIR);
        if (!workDir.exists()) {
            workDir.mkdirs();
        }
        File webApp = new File(M_WORKING_DIR, getApplicationId());
        File oldWebApp = new File(webApp.getAbsolutePath());
        FileUtils.deleteDirectory(oldWebApp);
        new ZipExporterImpl(createWebArchive()).exportTo(new File(M_WORKING_DIR + "/" + packageName()), true);
        return webApp;
    }

    public String getContextPath() {
        return "/" + getApplicationId();
    }

    private static String packageName() {
        return getApplicationId() + ".war";
    }

    private static WebArchive createWebArchive() {
        MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class);
        final EffectivePomMavenDependencyResolver effectivePomMavenDependencyResolver = resolver.loadEffectivePom("pom.xml").importAllDependencies();
        final File[] files1 = effectivePomMavenDependencyResolver.resolveAsFiles();

        for (File file : files1) {
            final String name = file.getName();
            if (name.endsWith(".war") && name.contains("solr")) {
                solr = file;
            }
        }
        return addWebResourcesTo(ShrinkWrap.create(WebArchive.class, packageName()).setWebXML(new File(WEBAPP_SRC, "WEB-INF/web.xml"))
                .addPackages(true, RootPackageMarker.class.getPackage())).addAsLibraries(files1);


    }

    public String getBaseUrl() {
        return "http://localhost:" + getPort() + getContextPath();
    }

    public static String getApplicationId() {
        return "haku";
    }

    public int getPort() {
        return port;
    }
}
