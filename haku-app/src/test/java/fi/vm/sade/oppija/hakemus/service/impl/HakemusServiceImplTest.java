package fi.vm.sade.oppija.hakemus.service.impl;

import fi.vm.sade.oppija.hakemus.converter.ApplicationToHakemusTyyppi;
import fi.vm.sade.oppija.hakemus.converter.ApplicationToHakutoiveTyyppi;
import fi.vm.sade.oppija.hakemus.dao.ApplicationQueryParameters;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.service.hakemus.HakemusService;
import fi.vm.sade.service.hakemus.schema.HakemusTyyppi;
import fi.vm.sade.service.hakemus.schema.HakutoiveTyyppi;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Mikko Majapuro
 */
public class HakemusServiceImplTest {

    private HakemusService hakemusService;
    private ApplicationService applicationService;
    private ConversionService conversionService;

    @Before
    public void setUp() {
        applicationService = mock(ApplicationService.class);
        Application app = new Application("1.2.3.4.5.1");
        Map<String, String> answers = new HashMap<String, String>();
        answers.put("preference1-Koulutus-id", "1.5.5.5.5.1234");
        answers.put("preference2-Koulutus-id", "1.5.5.5.5.4455");
        answers.put("preference3-Koulutus-id", "1.5.5.5.5.7777");
        app.addVaiheenVastaukset("hakutoiveet", answers);
        List<Application> applications = new ArrayList<Application>();
        applications.add(app);
        when(applicationService.findApplications(eq(""), any(ApplicationQueryParameters.class))).thenReturn(applications);
        when(applicationService.getApplicationsByApplicationOption(anyList())).thenReturn(applications);
        when(applicationService.getApplicationsByApplicationSystem(anyList())).thenReturn(applications);
        conversionService = mock(ConversionService.class);
        ApplicationToHakutoiveTyyppi applicationToHakutoiveTyyppi = new ApplicationToHakutoiveTyyppi();
        ApplicationToHakemusTyyppi applicationToHakemusTyyppi = new ApplicationToHakemusTyyppi();
        when(conversionService.convert(any(Object.class), eq(HakemusTyyppi.class))).thenReturn(applicationToHakemusTyyppi.convert(app));
        when(conversionService.convert(any(Object.class), eq(HakutoiveTyyppi.class))).thenReturn(applicationToHakutoiveTyyppi.convert(app));

        hakemusService = new HakemusServiceImpl(applicationService, conversionService);
    }

    @Test
    public void testHaeHakemukset() {
        List<String> oids = new ArrayList<String>();
        oids.add("1.2.3.4.3.999");
        List<HakemusTyyppi> hakemukset = hakemusService.haeHakemukset(oids);
        Assert.assertNotNull(hakemukset);
        Assert.assertEquals(1, hakemukset.size());
        Assert.assertEquals("1.2.3.4.5.1", hakemukset.get(0).getHakemusOid());
        verify(applicationService, times(1)).getApplicationsByApplicationOption(anyList());
        verify(conversionService, times(1)).convert(any(Object.class), eq(HakemusTyyppi.class));
    }

    @Test
    public void testHaeHakutoiveet() {
        List<String> oids = new ArrayList<String>();
        oids.add("yhteishaku");
        List<HakutoiveTyyppi> hakutoiveet = hakemusService.haeHakutoiveet(oids);
        Assert.assertNotNull(hakutoiveet);
        Assert.assertEquals(1, hakutoiveet.size());
        Assert.assertNotNull(hakutoiveet.get(0));
        Assert.assertEquals("1.2.3.4.5.1", hakutoiveet.get(0).getHakemusOid());
        Assert.assertEquals(3, hakutoiveet.get(0).getHakutoive().size());
        verify(applicationService, times(1)).getApplicationsByApplicationSystem(anyList());
        verify(conversionService, times(3)).convert(any(Object.class), eq(HakutoiveTyyppi.class));
    }
}
