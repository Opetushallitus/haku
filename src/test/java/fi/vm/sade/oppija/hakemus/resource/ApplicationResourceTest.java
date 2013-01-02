package fi.vm.sade.oppija.hakemus.resource;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.AnonymousUser;
import fi.vm.sade.oppija.lomake.domain.FormId;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationResourceTest {

    private ApplicationService applicationService;
    private ApplicationResource applicationResource;
    private Application application;

    private final String OID = "1.2.3.4.5.100";
    private final String ASID = "yhteishaku";

    @Before
    public void setUp() {
        this.applicationService = mock(ApplicationService.class);

        FormId formId = new FormId(ASID, "fi");

        Map<String, String> phase1 = new HashMap<String, String>();
        phase1.put("nimi", "Alan Turing");
        Map<String, Map<String, String>> phases = new HashMap<String, Map<String, String>>();
        phases.put("henkilotiedot", phase1);
        this.application = new Application(formId, new AnonymousUser(), phases);
        this.application.setOid(OID);

        when(applicationService.getApplication(OID)).thenReturn(this.application);

        ArrayList<Application> applications = new ArrayList<Application>();
        applications.add(this.application);
        when(applicationService.getApplicationsByApplicationSystem(ASID)).thenReturn(applications);

        this.applicationResource = new ApplicationResource(this.applicationService);
    }

    @Test
    public void testGetApplication() {
        Application a = this.applicationResource.getApplication(OID);
        assertEquals(this.application, a);
    }

    @Test
    public void testGetApplications() {
        List<Application> applications = this.applicationResource.getApplications(ASID);
        assertEquals(1, applications.size());
    }

}
