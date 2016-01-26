package fi.vm.sade.haku.oppija.hakemus.service.impl;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.impl.KoulutusinformaatioServiceMockImpl;
import fi.vm.sade.haku.oppija.common.organisaatio.impl.OrganizationServiceMockImpl;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.impl.SuoritusrekisteriServiceMockImpl;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Application.PaymentState;
import fi.vm.sade.haku.oppija.hakemus.resource.ApplicationResource;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.domain.*;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.service.impl.FormServiceImpl;
import fi.vm.sade.haku.oppija.lomake.service.mock.UserSessionMock;
import fi.vm.sade.haku.virkailija.authentication.impl.AuthenticationServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOid;
import fi.vm.sade.haku.virkailija.valinta.impl.ValintaServiceMockImpl;
import fi.vm.sade.hakutest.AuthedIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class ApplicationResourceTest extends AuthedIntegrationTest {

    public static final String OID = "1.2.3.4";
    public static final String OID2 = "1.2.3.4.1";
    public static final String OID3 = "1.2.3.4.2";

    private ApplicationResource applicationResource;

    @Before
    public void setup() {
        ApplicationService as = new ApplicationServiceImpl(
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
        applicationResource = new ApplicationResource(as, applicationSystemService, applicationOptionService, syntheticApplicationService, i18nBundleService);
        Calendar asPeriodStart = Calendar.getInstance();
        asPeriodStart.set(2015, Calendar.JANUARY, 1);
        Calendar asPeriodEnd = Calendar.getInstance();
        Calendar applicationSubmitted = Calendar.getInstance();
        applicationSubmitted.set(2015, Calendar.MARCH, 15);
        asPeriodEnd.set(2015, Calendar.DECEMBER, 3);
        ApplicationSystem applicationSystem = new ApplicationSystem(
            "haku1", new Form("haku1", ElementUtil.createI18NAsIs("haku1")), ElementUtil.createI18NAsIs("haku1"),
                "ACTIVE",
                Arrays.asList(
                        new ApplicationPeriod(asPeriodStart.getTime(), asPeriodEnd.getTime()),
                        new ApplicationPeriod(asPeriodStart.getTime(), asPeriodEnd.getTime())),
                OppijaConstants.HAKUTYYPPI_VARSINAINEN_HAKU,
                false,
                OppijaConstants.HAKUTAPA_JATKUVA_HAKU,
                2015,
                "",
                "",
                "",
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                3,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                new Date(),
                false);
        applicationSystemService.save(applicationSystem);

        Application t = new Application("haku1", new User("foo"), ApplicationOid.of(OID).getValue());
        t.setReceived(applicationSubmitted.getTime());
        t.setRequiredPaymentState(PaymentState.NOTIFIED);
        applicationDAO.save(t);

        // Another application system & application, application system open to foreseen future

        Calendar asPeriodStart2 = Calendar.getInstance();
        asPeriodStart2.set(2015, Calendar.JANUARY, 1);
        Calendar asPeriodEnd2 = Calendar.getInstance();
        Calendar applicationSubmitted2 = Calendar.getInstance();
        applicationSubmitted2.set(2016, Calendar.MARCH, 15);
        asPeriodEnd2.set(2999, Calendar.DECEMBER, 3);
        ApplicationSystem applicationSystem2 = new ApplicationSystem(
                "haku2", new Form("haku2", ElementUtil.createI18NAsIs("haku2")), ElementUtil.createI18NAsIs("haku2"),
                "ACTIVE",
                Arrays.asList(
                        new ApplicationPeriod(asPeriodStart2.getTime(), asPeriodEnd2.getTime()),
                        new ApplicationPeriod(asPeriodStart2.getTime(), asPeriodEnd2.getTime())),
                OppijaConstants.HAKUTYYPPI_VARSINAINEN_HAKU,
                false,
                OppijaConstants.HAKUTAPA_JATKUVA_HAKU,
                2015,
                "",
                "",
                "",
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                3,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                new Date(),
                false);
        applicationSystemService.save(applicationSystem2);

        Application t2 = new Application("haku2", new User("foo"), ApplicationOid.of(OID2).getValue());
        t2.setReceived(applicationSubmitted2.getTime());
        t2.setRequiredPaymentState(PaymentState.NOTIFIED);
        applicationDAO.save(t2);

        // Another application system & application system closed, application sent within the payment grace period

        Calendar asPeriodStart3 = Calendar.getInstance();
        asPeriodStart3.set(2015, Calendar.JANUARY, 1);
        Calendar applicationSubmitted3 = Calendar.getInstance();
        applicationSubmitted3.add(Calendar.DATE, -5);
        Calendar asPeriodEnd3 = Calendar.getInstance();
        asPeriodEnd3.add(Calendar.DATE, -1);
        ApplicationSystem applicationSystem3 = new ApplicationSystem(
                "haku3", new Form("haku3", ElementUtil.createI18NAsIs("haku3")), ElementUtil.createI18NAsIs("haku3"),
                "ACTIVE",
                Arrays.asList(
                        new ApplicationPeriod(asPeriodStart3.getTime(), asPeriodEnd3.getTime()),
                        new ApplicationPeriod(asPeriodStart3.getTime(), asPeriodEnd3.getTime())),
                OppijaConstants.HAKUTYYPPI_VARSINAINEN_HAKU,
                false,
                OppijaConstants.HAKUTAPA_JATKUVA_HAKU,
                2015,
                "",
                "",
                "",
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                3,
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                new Date(),
                false);
        applicationSystemService.save(applicationSystem3);

        Application t3 = new Application("haku3", new User("foo"), ApplicationOid.of(OID3).getValue());
        t3.setReceived(applicationSubmitted3.getTime());
        t3.setRequiredPaymentState(PaymentState.NOTIFIED);
        applicationDAO.save(t3);
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

    @Test
    public void testCannotSetPaymentStateToNotOKForActiveApplication() {
        applicationResource.setPaymentState(OID2, ImmutableMap.of("paymentState", "NOT_OK"));

        Application application = applicationResource.getApplicationByOid(OID2);

        assertEquals(PaymentState.NOTIFIED, application.getRequiredPaymentState());
    }

    @Test
    public void testCannotSetPaymentStateToNotOKForApplicationWithinPaymentGracePeriod() {
        applicationResource.setPaymentState(OID3, ImmutableMap.of("paymentState", "NOT_OK"));

        Application application = applicationResource.getApplicationByOid(OID3);

        assertEquals(PaymentState.NOTIFIED, application.getRequiredPaymentState());
    }
}
