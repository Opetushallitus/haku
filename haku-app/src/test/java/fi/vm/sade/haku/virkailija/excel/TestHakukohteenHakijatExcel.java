package fi.vm.sade.haku.virkailija.excel;

import fi.vm.sade.haku.oppija.hakemus.it.IntegrationTestSupport;
import fi.vm.sade.haku.oppija.hakemus.resource.ApplicationResource;
import fi.vm.sade.haku.oppija.hakemus.resource.DateParam;
import fi.vm.sade.haku.oppija.hakemus.resource.XlsModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.servlet.jsp.jstl.core.Config;
import java.util.List;
import java.util.Locale;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("it")
public class TestHakukohteenHakijatExcel extends IntegrationTestSupport {

    @Autowired
    ApplicationResource applicationResource;

    @Test
    public void testKorkeakouluExcel() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(Config.FMT_LOCALE + ".session", Locale.ENGLISH);

        String asid = "1.2.246.562.29.95390561488";
        String aoid = "";
        String aoidCode = "";
        String searchTerms = "";
        List<String> state = null;
        Boolean preferenceChecked = true;
        String lopoid = "";
        String aoOid = "1.2.246.562.20.92555013215";
        String groupOid = "";
        String baseEducation = "";
        Boolean discretionaryOnly = false;
        Boolean primaryPreferenceOnly = false;
        String sendingSchoolOid = "";
        String sendingClass = "";
        DateParam updatedAfter = null;
        int start = 0;
        int rows = 100;

        XlsModel model = applicationResource.getApplicationsByOids(
                request,
                asid,
                aoid,
                aoidCode,
                searchTerms,
                state,
                preferenceChecked,
                lopoid,
                aoOid,
                groupOid,
                baseEducation,
                discretionaryOnly,
                primaryPreferenceOnly,
                sendingSchoolOid,
                sendingClass,
                updatedAfter,
                start,
                rows
        );

        // TODO
        assertTrue(false);

    }

}
