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

import java.io.File;
import java.io.IOException;

/**
 * @author jukka
 * @version 9/18/1210:44 AM}
 * @since 1.1
 */
public class TomcatContainerTest {
    private static final String WEBAPP_SRC = "src/main/webapp";
    /**
     * The temporary directory in which Tomcat and the app are deployed.
     */
    private static String mWorkingDir = "target/tomcat";
    /**
     * The tomcat instance.
     */
    private Tomcat mTomcat;

    private static WebArchive addWebResourcesTo(WebArchive archive) {
        final File webAppDirectory = new File(WEBAPP_SRC);
        for (File file : org.apache.commons.io.FileUtils.listFiles(webAppDirectory, null, true)) {
            if (!file.isDirectory() && !file.getName().equals(".svn")) {
                archive.addAsWebResource(file, file.getPath().substring(WEBAPP_SRC.length()));
            }
        }
        return archive;
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
        mTomcat.setPort(0);
        mTomcat.setBaseDir(mWorkingDir);
        mTomcat.getHost().setAppBase(mWorkingDir);
        mTomcat.getHost().setAutoDeploy(true);
        mTomcat.getHost().setDeployOnStartup(true);
    }

    private void createWebApp() throws IOException {
        String contextPath = getContextPath();
        final File workDir = new File(mWorkingDir);
        if (!workDir.exists()) {
            boolean ignored = workDir.mkdirs();
        }
        File webApp = new File(mWorkingDir, getApplicationId());
        File oldWebApp = new File(webApp.getAbsolutePath());
        FileUtils.deleteDirectory(oldWebApp);
        new ZipExporterImpl(createWebArchive()).exportTo(new File(mWorkingDir + "/" + packageName()), true);
        mTomcat.addWebapp(mTomcat.getHost(), contextPath, webApp.getAbsolutePath());
    }

    private String getContextPath() {
        return "/" + getApplicationId();
    }

    private String packageName() {
        return getApplicationId() + ".war";
    }

    private WebArchive createWebArchive() {
        MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class);
        final File[] files = resolver.loadEffectivePom("pom.xml").importAllDependencies().resolveAsFiles();
        return addWebResourcesTo(ShrinkWrap.create(WebArchive.class, packageName()).setWebXML(new File(WEBAPP_SRC, "WEB-INF/web.xml"))
                .addPackages(true, RootPackageMarker.class.getPackage())).addAsLibraries(files);


    }

    public String getApplicationId() {
        return "haku";
    }

    public int getPort() {
        return mTomcat.getConnector().getLocalPort();
    }

    protected String getBaseUrl() {
        return "http://localhost:" + getPort() + getContextPath();
    }
}
