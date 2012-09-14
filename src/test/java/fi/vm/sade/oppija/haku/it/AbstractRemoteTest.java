package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.service.AdminService;
import fi.vm.sade.oppija.haku.tools.FileHandling;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * @author jukka
 * @version 9/13/123:42 PM}
 * @since 1.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public abstract class AbstractRemoteTest {

    @Autowired
    @Qualifier("remoteAdminService")
    private AdminService adminService;
    protected String path = "test-data.json";


    @Before
    public void init() throws IOException {
        final StringBuilder stringBuilder = new FileHandling().readFile(new ClassPathResource(path).getInputStream());
        adminService.replaceModel(stringBuilder.toString());
    }
}
