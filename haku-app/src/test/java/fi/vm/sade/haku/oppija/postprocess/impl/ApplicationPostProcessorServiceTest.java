package fi.vm.sade.haku.oppija.postprocess.impl;

import fi.vm.sade.haku.VirkailijaAuditLogger;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidatorFactory;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.authentication.PersonBuilder;
import fi.vm.sade.haku.virkailija.authentication.impl.AuthenticationServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApplicationPostProcessorServiceTest {


    private ApplicationPostProcessorService applicationPostProcessorService;
    private AuthenticationService authenticationService;

    Map<String, String> answerMap;
    private ElementTreeValidator elementTreeValidator;

    @Before
    public void setUp() {
        authenticationService = spy(new AuthenticationServiceMockImpl());
        ValidatorFactory validatorFactory = mock(ValidatorFactory.class);
        elementTreeValidator = new ElementTreeValidator(validatorFactory);

        final ApplicationService applicationService = null;
        final BaseEducationService baseEducationService = null;
        final FormService formService = null;
        final HakuService hakuService = null;
        final ApplicationSystemService applicationSystemService = null;
        final HakumaksuService hakumaksuService = null;
        final VirkailijaAuditLogger virkailijaAuditLogger = mock(VirkailijaAuditLogger.class);
        final PaymentDueDateProcessingWorker paymentDueDateProcessingWorker = null;

        applicationPostProcessorService = new ApplicationPostProcessorService(applicationService, applicationSystemService, baseEducationService, formService,
                elementTreeValidator, authenticationService, hakuService, hakumaksuService, paymentDueDateProcessingWorker, virkailijaAuditLogger);
        applicationPostProcessorService.setRetryFailQuickCount(5);
        applicationPostProcessorService.setRetryFailedAgainTime(10000);

        answerMap = new HashMap<>();
        answerMap.put(OppijaConstants.ELEMENT_ID_FIRST_NAMES, "Etunimi");
        answerMap.put(OppijaConstants.ELEMENT_ID_NICKNAME, "Etunimi");
        answerMap.put(OppijaConstants.ELEMENT_ID_LAST_NAME, "Sukunimi");
        answerMap.put(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER, "030506-229W");
        answerMap.put(OppijaConstants.ELEMENT_ID_SEX, "Mies");
        answerMap.put(OppijaConstants.ELEMENT_ID_HOME_CITY, "Kaupunki");
        answerMap.put(OppijaConstants.ELEMENT_ID_LANGUAGE, "fi");
        answerMap.put(OppijaConstants.ELEMENT_ID_NATIONALITY, OppijaConstants.NATIONALITY_CODE_FI);
        answerMap.put(OppijaConstants.ELEMENT_ID_FIRST_LANGUAGE, "fi");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWillNotMergePersonsWithDifferentSSNs() {
        Person p1 = createPerson("Etunimi", "Etunimi", "Sukunimi", "070187-951E",
                "070187", false, "etu.nimi@example.org", "Mies", "Kaupunki",
                false, "fi", OppijaConstants.NATIONALITY_CODE_FI, "fi");
        Person p2 = createPerson("Etunimi", "Etunimi", "Sukunimi", "070187-949C",
                "070187", false, "etu.nimi@example.org", "Mies", "Kaupunki",
                false, "fi", OppijaConstants.NATIONALITY_CODE_FI, "fi");
        p1.mergeWith(p2);
    }

    @Test
    public void testWillMergePersonsWithEqualSSNs() {
        Person p1 = createPerson("Etunimi", "Etunimi", "Sukunimi", "070187-949C",
                "070187", false, "etu.nimi@example.org", "Mies", "Kaupunki",
                false, "fi", OppijaConstants.NATIONALITY_CODE_FI, "fi");
        Person p2 = createPerson("Etunimi", "Etunimi", "Sukunimi", "070187-949C",
                "070187", false, "testi@example.org", "Mies", "Kaupunki",
                false, "fi", OppijaConstants.NATIONALITY_CODE_FI, "fi");
        p1.mergeWith(p2);
        assertEquals(p1.getEmail(), "testi@example.org");
    }

    private Person createPerson(String firstNames, String nickName, String lastName, String socialSecurityNumber,
                              String dateOfBirth, Boolean noSocialSecurityNumber, String email, String sex, String homeCity,
                              Boolean securityOrder, String language, String nationality, String contactLanguage) {
        return new Person(firstNames, nickName, lastName, socialSecurityNumber,
                dateOfBirth, noSocialSecurityNumber, email, sex, homeCity,
                securityOrder, language, nationality, contactLanguage,
                "", "", null, null, null, null, null);
    }

    @Test
    public void testSetPersonFi() {
        Application application = new Application();

        application.setVaiheenVastauksetAndSetPhaseId("henkilotiedot", answerMap);
        application = applicationPostProcessorService.addPersonOid(application, "test");
        assertNotNull("PersonOid should not be null", application.getPersonOid());

        verify(authenticationService, times(1)).addPerson((Person) anyObject());
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    public void testSetPersonNotFi() {
        Application application = new Application();
        answerMap.remove(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER);
        answerMap.put(OppijaConstants.ELEMENT_ID_NATIONALITY, "swe");
        application.setVaiheenVastauksetAndSetPhaseId("henkilotiedot", answerMap);
        application = applicationPostProcessorService.addPersonOid(application, "test");
        assertNotNull("PersonOid should not be null", application.getPersonOid());

        verify(authenticationService, times(1)).addPerson((Person) anyObject());
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    public void testCheckPersonOidMissing(){
        Application application = new Application();
        application.setVaiheenVastauksetAndSetPhaseId("henkilotiedot", answerMap);
        application.flagStudentIdentificationRequired();

        when(authenticationService.addPerson(any(Person.class))).thenReturn(PersonBuilder.start().setPersonOid("1.2.3").setStudentOid("1.2.3").get());
        final Application modified = applicationPostProcessorService.checkStudentOid(application.clone());

        assertNotNull(modified.getPersonOid());
        assertNotNull(modified.getStudentOid());
        assertNull(modified.getStudentIdentificationDone());

        verify(authenticationService, times(1)).addPerson((Person) anyObject());
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    public void testCheckStudentOidMissing(){
        Application application = new Application();
        application.setVaiheenVastauksetAndSetPhaseId("henkilotiedot", answerMap);
        application.setPersonOid("1.2.3");
        application.flagStudentIdentificationRequired();

        when(authenticationService.addPerson(any(Person.class))).thenReturn(PersonBuilder.start().setPersonOid("1.2.3").setStudentOid("1.2.3").get());
        final Application modified = applicationPostProcessorService.checkStudentOid(application.clone());

        assertEquals(application.getPersonOid(), modified.getPersonOid());
        assertNotNull(modified.getPersonOid());
        assertNull(modified.getStudentIdentificationDone());

        verify(authenticationService, times(1)).addPerson((Person) anyObject());
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    public void testCheckStudentOidMissingAfterRun(){
        Application application = new Application();
        application.setVaiheenVastauksetAndSetPhaseId("henkilotiedot", answerMap);
        application.setPersonOid("1.2.3");
        application.flagStudentIdentificationRequired();

        final Application modified = applicationPostProcessorService.checkStudentOid(application.clone());

        assertNotEquals(application.getAutomatedProcessingFailCount(), modified.getAutomatedProcessingFailCount());
        assertNotNull(modified.getStudentIdentificationDone());

        verify(authenticationService, times(1)).addPerson(any(Person.class));
        verifyNoMoreInteractions(authenticationService);
    }

    @Test
    public void testCheckStudentOidAlreadySetButAuthenticationServiceRetunsDifferentPersonOid(){
        final Application application = new Application();
        application.setVaiheenVastauksetAndSetPhaseId("henkilotiedot", answerMap);
        application.setPersonOid("1.2.3");
        application.setStudentOid("1.2.3");
        application.flagStudentIdentificationRequired();

        final Application modified = applicationPostProcessorService.checkStudentOid(application.clone());

        assertNotEquals(application.getPersonOid(), modified.getPersonOid());
        assertEquals(application.getStudentOid(), modified.getStudentOid());
        assertNull(modified.getStudentIdentificationDone());

        verify(authenticationService, times(1)).addPerson(any(Person.class));
        verifyNoMoreInteractions(authenticationService);
    }


    @Test
    public void testFailCountIncrementTransitionToSlowDown() {
        Application application = new Application();
        application.setVaiheenVastauksetAndSetPhaseId("henkilotiedot", answerMap);
        application.setPersonOid("1.2.3");
        application.flagStudentIdentificationRequired();

        when(authenticationService.getHenkilo(anyString())).thenReturn(null);
        Application modified = applicationPostProcessorService.checkStudentOid(application.clone());
        assertEquals(new Integer(1), modified.getAutomatedProcessingFailCount());

        for(int i=0;i<10;i++) {
            modified = applicationPostProcessorService.checkStudentOid(modified);
        }
        assertEquals(new Integer(5), modified.getAutomatedProcessingFailCount());
    }

    @Test
    public void testFailCountSlowDown() {
        Application application = new Application();
        application.setVaiheenVastauksetAndSetPhaseId("henkilotiedot", answerMap);
        application.setPersonOid("1.2.3");
        application.setLastAutomatedProcessingTime(System.currentTimeMillis());
        application.setAutomatedProcessingFailCount(20);
        application.setAutomatedProcessingFailRetryTime(System.currentTimeMillis());

        final Application modified = applicationPostProcessorService.checkStudentOid(application.clone());
        verifyZeroInteractions(authenticationService);
        assertEquals(new Integer(20), modified.getAutomatedProcessingFailCount());

        application.setAutomatedProcessingFailRetryTime(System.currentTimeMillis()-20000);
        final Application modified2 = applicationPostProcessorService.checkStudentOid(application.clone());
        verify(authenticationService, times(1)).addPerson(any(Person.class));
        assertEquals(new Integer(21), modified2.getAutomatedProcessingFailCount());
    }


}
