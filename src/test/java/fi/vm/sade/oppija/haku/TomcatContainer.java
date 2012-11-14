package fi.vm.sade.oppija.haku;

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

    /**
     * The temporary directory in which Tomcat and the app are deployed.
     */
    private static String mWorkingDir = "target/tomcat";
    /**
     * The tomcat instance.
     */
    private Tomcat mTomcat;
    private static File solr;
    private int port;

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

    public TomcatContainer() throws IOException, LifecycleException {
        createTomcat();
        createWebApp(createPackage());
        mTomcat.start();
        port = mTomcat.getConnector().getLocalPort();
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
        mTomcat.setBaseDir(mWorkingDir);
        mTomcat.getHost().setAppBase(mWorkingDir);
        mTomcat.getHost().setAutoDeploy(true);
        mTomcat.getHost().setDeployOnStartup(true);
    }


    private void createWebApp(File webApp) throws IOException {
        mTomcat.addWebapp(mTomcat.getHost(), getContextPath(), webApp.getAbsolutePath());


        prepareSolr();
        mTomcat.addWebapp(mTomcat.getHost(), "/solr", solr.getAbsolutePath());

    }

    private void prepareSolr() throws IOException {
        final File target = new File("target");
        System.setProperty("solr.solr.home", new File("target/resources/solr").getAbsolutePath());
        org.apache.commons.io.FileUtils.copyDirectoryToDirectory(new File(RESOURCES_SRC), target);

        System.setProperty("tarjonta.index.url", "http://localhost:" + getPort() + "/solr/");
        System.setProperty("tarjonta.data.url", "http://localhost:" + getPort() + "/haku/tarjontadev/learningDownloadPOC.xml");

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
