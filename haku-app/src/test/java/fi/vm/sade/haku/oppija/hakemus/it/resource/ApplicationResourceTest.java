package fi.vm.sade.haku.oppija.hakemus.it.resource;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.ApiAuditLogger;
import fi.vm.sade.haku.VirkailijaAuditLogger;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.impl.KoulutusinformaatioServiceMockImpl;
import fi.vm.sade.haku.oppija.common.organisaatio.impl.OrganizationServiceMockImpl;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.impl.SuoritusrekisteriServiceMockImpl;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Application.PaymentState;
import fi.vm.sade.haku.oppija.hakemus.resource.ApplicationResource;
import fi.vm.sade.haku.oppija.hakemus.resource.JSONException;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.impl.ApplicationServiceImpl;
import fi.vm.sade.haku.oppija.hakemus.service.impl.HakuPermissionServiceMockImpl;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.service.mock.UserSessionMock;
import fi.vm.sade.haku.oppija.ui.service.OfficerUIService;
import fi.vm.sade.haku.virkailija.authentication.impl.AuthenticationServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOid;
import fi.vm.sade.haku.virkailija.valinta.impl.ValintaServiceMockImpl;
import fi.vm.sade.hakutest.AuthedIntegrationTest;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ApplicationResourceTest extends AuthedIntegrationTest {

    public static final String OID = "1.2.3.4";
    public static final String OID_WO_PAYMENT_STATE = "1.2.3.4.1";
    public static final String OID_WO_PAYMENT_DUE_DATE = "1.2.3.4.2";
    public static final String OID_PAYMENT_DUE_DATE_IN_FUTURE = "1.2.3.4.3";
    public static final String OID_INVALID = "foobar";
    public static final String OID_DOES_NOT_EXIST = "1.2.3.4.111";

    private ApplicationResource applicationResource;

    @Before
    public void setup() {
        ApplicationService as = new ApplicationServiceImpl(
                applicationDAO,
                new UserSessionMock("foo"),
                null,
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
                "false",
                mock(ApiAuditLogger.class)
        );
        applicationResource = new ApplicationResource(as, applicationSystemService, applicationOptionService, syntheticApplicationService, i18nBundleService,
                mock(OfficerUIService.class), mock(VirkailijaAuditLogger.class));
    }

    @Test
    public void testSetPaymentState() {
        Application t = new Application("1.2.3", new User("foo"), ApplicationOid.of(OID).getValue());
        t.setPaymentDueDate(new Date(0));
        t.setRequiredPaymentState(PaymentState.NOTIFIED);
        applicationDAO.save(t);

        applicationResource.setPaymentState(OID, ImmutableMap.of("paymentState", "OK"));

        Application application = applicationResource.getApplicationByOid(mock(HttpServletRequest.class), OID);

        assertEquals(PaymentState.OK, application.getRequiredPaymentState());

        Map<String, String> changes = application.getHistory().get(0).getChanges().get(0);
        assertEquals("requiredPaymentState", changes.get("field"));
        assertEquals("NOTIFIED", changes.get("old value"));
        assertEquals("OK", changes.get("new value"));

        applicationResource.setPaymentState(OID, ImmutableMap.of("paymentState", "NOT_OK"));

        Application application2 = applicationResource.getApplicationByOid(mock(HttpServletRequest.class), OID);

        assertEquals(PaymentState.NOT_OK, application2.getRequiredPaymentState());
    }

    @Test
    public void testPaymentStateMustExist() {
        Application applicationWithOutPaymentState = new Application("1.2.3", new User("foo"), ApplicationOid.of(OID_WO_PAYMENT_STATE).getValue());
        applicationWithOutPaymentState.setPaymentDueDate(new Date(0));
        applicationDAO.save(applicationWithOutPaymentState);
        try {
            applicationResource.setPaymentState(OID_WO_PAYMENT_STATE, ImmutableMap.of("paymentState", "OK"));
        } catch (JSONException e) {
            assertEquals(Response.Status.FORBIDDEN.getStatusCode(), e.getResponse().getStatus());
        }
    }

    @Test
    public void testPaymentDueDateMustExist() {
        Application applicationWithOutPaymentDueDate = new Application("1.2.3", new User("foo"), ApplicationOid.of(OID_WO_PAYMENT_DUE_DATE).getValue());
        applicationWithOutPaymentDueDate.setPaymentDueDate(new Date(0));
        applicationDAO.save(applicationWithOutPaymentDueDate);
        try {
            applicationResource.setPaymentState(OID_WO_PAYMENT_DUE_DATE, ImmutableMap.of("paymentState", "OK"));
        } catch (JSONException e) {
            assertEquals(Response.Status.FORBIDDEN.getStatusCode(), e.getResponse().getStatus());
        }
    }


    @Test
    public void testPaymentStateNotChangedWhenDueDateInFuture() {
        Application application = new Application("1.2.3", new User("foo"), ApplicationOid.of(OID_PAYMENT_DUE_DATE_IN_FUTURE).getValue());
        application.setPaymentDueDate(new LocalDateTime().plusDays(1).toDate());
        application.setRequiredPaymentState(PaymentState.NOTIFIED);
        applicationDAO.save(application);
        applicationResource.setPaymentState(OID_PAYMENT_DUE_DATE_IN_FUTURE, ImmutableMap.of("paymentState", "NOT_OK"));
        Application result = applicationDAO.getApplication(OID_PAYMENT_DUE_DATE_IN_FUTURE);
        assertEquals(PaymentState.NOTIFIED, result.getRequiredPaymentState());
    }



    @Test
    public void testInvalidOidAsArgument() {
        try {
            applicationResource.setPaymentState(OID_INVALID, ImmutableMap.of("paymentState", "OK"));
        } catch (JSONException e) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), e.getResponse().getStatus());
        }
    }

    @Test
    public void testNonexistingOidAsArgument() {
        try {
            applicationResource.setPaymentState(OID_DOES_NOT_EXIST, ImmutableMap.of("paymentState", "OK"));
        } catch (JSONException e) {
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), e.getResponse().getStatus());
        }
    }

}
