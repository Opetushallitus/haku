package fi.vm.sade.haku.oppija.hakemus.service.impl;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.impl.KoulutusinformaatioServiceMockImpl;
import fi.vm.sade.haku.oppija.common.organisaatio.impl.OrganizationServiceMockImpl;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.impl.SuoritusrekisteriServiceMockImpl;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Application.PaymentState;
import fi.vm.sade.haku.oppija.hakemus.resource.ApplicationResource;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.service.impl.FormServiceImpl;
import fi.vm.sade.haku.oppija.lomake.service.mock.UserSessionMock;
import fi.vm.sade.haku.virkailija.authentication.impl.AuthenticationServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.Oid;
import fi.vm.sade.haku.virkailija.valinta.impl.ValintaServiceMockImpl;
import fi.vm.sade.hakutest.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ApplicationResourceTest extends IntegrationTest {

    public static final String OID = "1.2.3";
    ApplicationService applicationService;
    ApplicationResource applicationResource;


    @Before
    public void setup() {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("", "", "ROLE_APP_HAKEMUS_CRUD"));
        applicationService = new ApplicationServiceImpl(
                applicationDAO,
                new UserSessionMock("foo"),
                new FormServiceImpl(applicationSystemService),
                applicationOidService,
                new AuthenticationServiceMockImpl(),
                new OrganizationServiceMockImpl(),
                new HakuPermissionServiceMockImpl(),
                applicationSystemService,
                new KoulutusinformaatioServiceMockImpl(),
                i18nBundleService,
                new SuoritusrekisteriServiceMockImpl(),
                hakuService,
                elementTreeValidator,
                new ValintaServiceMockImpl(),
                ohjausparametritService,
                "true",
                "false"
        );
        applicationResource = new ApplicationResource(applicationService, applicationSystemService, applicationOptionService, syntheticApplicationService, i18nBundleService);

        Application t = new Application(Oid.of(OID).getValue());
        t.setRequiredPaymentState(PaymentState.NOTIFIED);
        applicationDAO.save(t);
    }

    @Test
    public void testSetPaymentState() {
        applicationResource.setPaymentState(OID, ImmutableMap.of("paymentState", "OK"));

        Application application = applicationResource.getApplicationByOid(OID);

        assertEquals(PaymentState.OK, application.getRequiredPaymentState());

        Map<String, String> changes = application.getHistory().get(0).getChanges().get(0);
        assertEquals("requiredPaymentState", changes.get("field"));
        assertEquals("NOTIFIED", changes.get("old value"));
        assertEquals("OK", changes.get("new value"));

        applicationResource.setPaymentState(OID, ImmutableMap.of("paymentState", "NOT_OK"));

        Application application2 = applicationResource.getApplicationByOid(OID);

        assertEquals(PaymentState.NOT_OK, application2.getRequiredPaymentState());
    }

}
