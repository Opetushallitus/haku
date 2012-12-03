package fi.vm.sade.oppija.lomake.dao;

import com.mongodb.util.JSONParseException;
import fi.vm.sade.oppija.lomake.service.AdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * @author jukka
 * @version 9/13/122:11 PM}
 * @since 1.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class AdminServiceTest {
    @Autowired
    AdminService adminService;

    @Test(expected = JSONParseException.class)
    public void testInvalidContent() throws IOException {
        adminService.replaceModel(new MockMultipartFile("unvalidFile", new byte[]{123, 69, 70}).getInputStream());
    }

    @Test
    public void testValidContent() throws IOException {
        adminService.replaceModel(new ClassPathResource("test-data.json").getInputStream());
    }

}

