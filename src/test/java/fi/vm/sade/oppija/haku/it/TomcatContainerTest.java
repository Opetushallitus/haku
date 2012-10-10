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

package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.RootPackageMarker;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.setTextField;
import static net.sourceforge.jwebunit.junit.JWebUnit.submit;

/**
 * @author jukka
 * @version 9/18/1210:44 AM}
 * @since 1.1
 */
public class TomcatContainerTest {
    private static final String WEBAPP_SRC = "src/main/webapp";
    private static final String RESOURCES_SRC = "src/main/resources";

    /**
     * The temporary directory in which Tomcat and the app are deployed.
     */
    private static String mWorkingDir = "target/tomcat";
    private static final int PORT = 12456;
    /**
     * The tomcat instance.
     */
    private Tomcat mTomcat;
    private static File webApp;
    private static File solr;

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

    @Before
    public void setup() throws Throwable {
        createTomcat();
        createWebApp();
        mTomcat.start();

    }

    @After
    public void stop() throws Exception {
        if (mTomcat.getServer() != null
                && mTomcat.getServer().getState() != LifecycleState.DESTROYED) {
            if (mTomcat.getServer().getState() != LifecycleState.STOPPED) {
                mTomcat.stop();
            }
            mTomcat.destroy();
        }
    }


    private void createTomcat() {
        mTomcat = new Tomcat();
        mTomcat.setPort(getPort());

        mTomcat.setBaseDir(mWorkingDir);
        mTomcat.getHost().setAppBase(mWorkingDir);
        mTomcat.getHost().setAutoDeploy(true);
        mTomcat.getHost().setDeployOnStartup(true);
    }

    @BeforeClass
    public static void doPackage() throws IOException {
        webApp = createPackage();
    }

    private void createWebApp() throws IOException {
        mTomcat.addWebapp(mTomcat.getHost(), getContextPath(), webApp.getAbsolutePath());


        prepareSolr();
        mTomcat.addWebapp(mTomcat.getHost(), "/solr", solr.getAbsolutePath());

    }

    private void prepareSolr() throws IOException {
        final File target = new File("target");
        System.setProperty("solr.solr.home", new File("target/resources/solr").getAbsolutePath());
        org.apache.commons.io.FileUtils.copyDirectoryToDirectory(new File(RESOURCES_SRC), target);

        System.setProperty("tarjonta.index.url", "http://localhost:" + getPort() + "/solr/");

        for (File file : org.apache.commons.io.FileUtils.listFiles(target, null, true)) {
            if (file.getName().equals(".svn")) {
                file.delete();
            }
        }
    }

    private static File createPackage() throws IOException {
        final File workDir = new File(mWorkingDir);
        if (!workDir.exists()) {
            boolean ignored = workDir.mkdirs();
        }
        File webApp = new File(mWorkingDir, getApplicationId());
        File oldWebApp = new File(webApp.getAbsolutePath());
        FileUtils.deleteDirectory(oldWebApp);
        new ZipExporterImpl(createWebArchive()).exportTo(new File(mWorkingDir + "/" + packageName()), true);
        return webApp;
    }

    private String getContextPath() {
        return "/" + getApplicationId();
    }

    private static String packageName() {
        return getApplicationId() + ".war";
    }

    private static WebArchive createWebArchive() {
        MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class);
        final File[] files = resolver.loadEffectivePom("pom.xml").importAllDependencies().resolveAsFiles();

        for (File file : files) {
            final String name = file.getName();
            if (name.endsWith(".war") && name.contains("solr")) {
                solr = file;
            }
        }
        return addWebResourcesTo(ShrinkWrap.create(WebArchive.class, packageName()).setWebXML(new File(WEBAPP_SRC, "WEB-INF/web.xml"))
                .addPackages(true, RootPackageMarker.class.getPackage())).addAsLibraries(files);


    }

    public static String getApplicationId() {
        return "haku";
    }

    public int getPort() {
        return PORT;
    }

    public String getBaseUrl() {
        return "http://localhost:" + getPort() + getContextPath();
    }

    protected void login() {
        setTextField("j_username", "admin");
        setTextField("j_password", "admin");
        submit();
    }
}
