package fi.vm.sade.haku.oppija.hakemus.it;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationAttachment;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationAttachmentRequest;
import fi.vm.sade.haku.oppija.hakemus.it.IntegrationTestSupport;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class ApplicationServiceImplIT extends IntegrationTestSupport {

    private ApplicationService applicationService;

    @Before
    public void setUp() throws Exception {
        applicationService = appContext.getBean(ApplicationService.class);
    }

    @Test
    public void retainAttachmentRequestReceptionTest() {
        // Application with PreferenceAoId attachment request
        Application application = IntegrationTestSupport.getTestApplication("1.2.246.562.11.00000000178");
        ApplicationAttachmentRequest attReq = application.getAttachmentRequests().get(0);
        attReq.setReceptionStatus(ApplicationAttachmentRequest.ReceptionStatus.ARRIVED);
        attReq.setProcessingStatus(ApplicationAttachmentRequest.ProcessingStatus.CHECKED);
        assertEquals(ApplicationAttachmentRequest.ReceptionStatus.ARRIVED, application.getAttachmentRequests().get(0).getReceptionStatus());

        application = applicationService.updatePreferenceBasedData(application).getApplication();
        assertEquals(ApplicationAttachmentRequest.ReceptionStatus.ARRIVED, application.getAttachmentRequests().get(0).getReceptionStatus());
        assertEquals(ApplicationAttachmentRequest.ProcessingStatus.CHECKED, application.getAttachmentRequests().get(0).getProcessingStatus());

        // Application with PreferenceAoGroupId attachment request
        application = IntegrationTestSupport.getTestApplication("1.2.246.562.11.00004102043");
        attReq = application.getAttachmentRequests().get(0);
        attReq.setReceptionStatus(ApplicationAttachmentRequest.ReceptionStatus.ARRIVED);
        attReq = application.getAttachmentRequests().get(1);
        attReq.setReceptionStatus(ApplicationAttachmentRequest.ReceptionStatus.ARRIVED_LATE);

        application = applicationService.updatePreferenceBasedData(application).getApplication();
        assertEquals(ApplicationAttachmentRequest.ReceptionStatus.ARRIVED, application.getAttachmentRequests().get(0).getReceptionStatus());
        assertEquals(ApplicationAttachmentRequest.ReceptionStatus.ARRIVED_LATE, application.getAttachmentRequests().get(1).getReceptionStatus());

    }


}
