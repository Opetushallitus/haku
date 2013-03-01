package fi.vm.sade.oppija.hakemus.converter;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.service.hakemus.schema.HakutoiveTyyppi;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Mikko Majapuro
 */
public class ApplicationToHakutoiveTyyppiTest {

    private ApplicationToHakutoiveTyyppi converter = new ApplicationToHakutoiveTyyppi();

    @Test
    public void testConvert() {
        Application application = new Application("1.2.3.4.5.1");
        application.setPersonOid("1.3.3.3.3.4444");
        Map<String, String> answers = new HashMap<String, String>();
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

        HakutoiveTyyppi hakutoive = converter.convert(application);
        assertNotNull(hakutoive);
        assertEquals(application.getOid(), hakutoive.getHakemusOid());
        assertEquals(3, hakutoive.getHakutoive().size());
        assertEquals(1, hakutoive.getHakutoive().get(0).getPrioriteetti());
        assertEquals("1.5.5.5.5.1234", hakutoive.getHakutoive().get(0).getHakukohdeOid());
        assertEquals(2, hakutoive.getHakutoive().get(1).getPrioriteetti());
        assertEquals("1.5.5.5.5.4455", hakutoive.getHakutoive().get(1).getHakukohdeOid());
        assertEquals(3, hakutoive.getHakutoive().get(2).getPrioriteetti());
        assertEquals("1.5.5.5.5.7777", hakutoive.getHakutoive().get(2).getHakukohdeOid());
    }
}
