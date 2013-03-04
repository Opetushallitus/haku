package fi.vm.sade.oppija.hakemus.converter;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.service.hakemus.schema.HakemusTyyppi;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Mikko Majapuro
 */
public class ApplicationToHakemusTyyppiTest {

    private ApplicationToHakemusTyyppi converter = new ApplicationToHakemusTyyppi();


    @Test
    public void testConvert() {
        Application application = new Application("1.2.3.4.5.1");
        application.setPersonOid("1.3.3.3.3.4444");
        Map<String, String> answers = new HashMap<String, String>();
        answers.put("Etunimet", "Aaro Eero");
        answers.put("Sukunimi", "Testaaja");
        answers.put("key1", "value1");
        answers.put("key2", "value2");
        answers.put("key3", "value3");
        answers.put("preference1-Koulutus-id", "1.5.5.5.5.1234");
        answers.put("preference2-Koulutus-id", "1.5.5.5.5.4455");
        answers.put("preference3-Koulutus-id", "1.5.5.5.5.7777");
        application.addVaiheenVastaukset("test", answers);
        Map<String, String> additionalData = new HashMap<String, String>();
        additionalData.put("extra1", "extraVal1");
        additionalData.put("extra2", "extraVal2");
        additionalData.put("extra3", "extraVal3");
        application.setAdditionalInfo(additionalData);

        HakemusTyyppi hakemus = converter.convert(application);
        assertNotNull(hakemus);
        assertEquals(application.getOid(), hakemus.getHakemusOid());
        assertEquals(application.getPersonOid(), hakemus.getHakijaOid());
        assertEquals("Aaro Eero", hakemus.getHakijanEtunimi());
        assertEquals("Testaaja", hakemus.getHakijanSukunimi());
        assertEquals(3, hakemus.getHakutoive().size());
        assertEquals(1, hakemus.getHakutoive().get(0).getPrioriteetti());
        assertEquals("1.5.5.5.5.1234", hakemus.getHakutoive().get(0).getHakukohdeOid());
        assertEquals(2, hakemus.getHakutoive().get(1).getPrioriteetti());
        assertEquals("1.5.5.5.5.4455", hakemus.getHakutoive().get(1).getHakukohdeOid());
        assertEquals(3, hakemus.getHakutoive().get(2).getPrioriteetti());
        assertEquals("1.5.5.5.5.7777", hakemus.getHakutoive().get(2).getHakukohdeOid());

        assertEquals(11, hakemus.getAvainArvo().size());
    }
}
