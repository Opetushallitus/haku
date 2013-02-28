package fi.vm.sade.oppija.hakemus.service.impl;

import fi.vm.sade.oppija.hakemus.dao.ApplicationQueryParameters;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.service.hakemus.HakemusService;
import fi.vm.sade.service.hakemus.schema.HakemusTyyppi;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.List;

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
        List<Application> applications = new ArrayList<Application>();
        applications.add(app);
        when(applicationService.findApplications(eq(""), any(ApplicationQueryParameters.class))).thenReturn(applications);
        when(applicationService.getApplicationsByApplicationOption(anyList())).thenReturn(applications);
        conversionService = mock(ConversionService.class);
        HakemusTyyppi hakemus = new HakemusTyyppi();
        hakemus.setHakemusOid(app.getOid());
        when(conversionService.convert(any(Object.class), eq(HakemusTyyppi.class))).thenReturn(hakemus);
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
}
