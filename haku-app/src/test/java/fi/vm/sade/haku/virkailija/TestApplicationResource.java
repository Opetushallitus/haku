package fi.vm.sade.haku.virkailija;

import fi.vm.sade.haku.oppija.hakemus.it.IntegrationTestSupport;
import fi.vm.sade.haku.oppija.hakemus.resource.ApplicationResource;
import fi.vm.sade.haku.oppija.hakemus.resource.XlsModel;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
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

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("it")
public class TestApplicationResource extends IntegrationTestSupport {

    @Autowired
    ApplicationResource applicationResource;

    @Test
    public void testKorkeakouluExcel() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(Config.FMT_LOCALE + ".session", new Locale("fi"));

        XlsModel xlsModel = applicationResource.getApplicationsByOids(
                request, "1.2.246.562.29.95390561488", "test", "", "", null,
                null, "", "1.2.246.562.20.92555013215", "", "", false, false,
                "", "", null, 0, 100);

        String[][] expected = new String[][]{
                {"1.2.246.562.11.00001355851", "1.2.246.562.24.14229104472", "", "Vitsivuori", "Elias II", "Elias", "Ei", "Kyllä", "mies", "01.01.1901", null, "010101-123N", "hakija-123969@oph.fi", "050 11440811", "Suomi", null, null, null, "Kuumakallio 271", "02210", "englanti", "X", "2012", "Higher Secondary Education", "Texas International Higher Secondary School", "XXX", "Kyllä", "X", "X", "X", "X", "Englanti", "1", "Hakukelpoinen", "Kopio", "Saapunut", "Tarkistettu"},
                {"1.2.246.562.11.00001544594", "1.2.246.562.24.39736979832", "X", "Heppalahti", "Joel IX", "Joel", "Ei", "Ei", "nainen", "19.09.1988", "Dhading, Nepal", null, "hakija-19995@oph.fi", "050 1813961", null, "Lorem osoite", "44600", "Kathmandu", null, null, "englanti", "X", "2014", "Proficiency Certificate Level in Nursing", "Green Tara College of Health Science", "XXX", "Kyllä", "X", "X", "X", "X", "Englanti", "1", "Ei hakukelpoinen", "Tuntematon", "Saapunut", "Tarkistettu"}
        };

        List<String> rowKeys = xlsModel.rowKeyList();
        List<Element> colKeys = xlsModel.columnKeyList();
        for (int row = 0; row < rowKeys.size(); row++) {
            for (int col = 0; col < colKeys.size(); col++) {
                assertEquals("Compared row " + row + ", col " + col, expected[row][col], xlsModel.getValue(rowKeys.get(row), colKeys.get(col)));
            }
        }

    }

}
