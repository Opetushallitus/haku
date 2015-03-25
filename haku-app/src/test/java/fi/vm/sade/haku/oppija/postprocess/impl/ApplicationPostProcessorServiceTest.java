package fi.vm.sade.haku.oppija.postprocess.impl;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidatorFactory;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.impl.AuthenticationServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class ApplicationPostProcessorServiceTest {


    private ApplicationPostProcessorService applicationPostProcessorService;
    private ApplicationDAO applicationDAO;
    private AuthenticationService authenticationService;

    Map<String, String> answerMap;
    private ElementTreeValidator elementTreeValidator;

    @Before
    public void setUp() {
        authenticationService = new AuthenticationServiceMockImpl();
        ValidatorFactory validatorFactory = mock(ValidatorFactory.class);
        elementTreeValidator = new ElementTreeValidator(validatorFactory);
        applicationDAO = mock(ApplicationDAO.class);

        final ApplicationService applicationService = null;
        final BaseEducationService baseEducationService = null;
        final FormService formService = null;

        applicationPostProcessorService = new ApplicationPostProcessorService(applicationService, baseEducationService, formService, elementTreeValidator, authenticationService, applicationDAO);
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
    
    @Test
    public void testSetPersonFi() {
        Application application = new Application();

        application.addVaiheenVastaukset("henkilotiedot", answerMap);
        application = applicationPostProcessorService.addPersonOid(application);
        assertNotNull("PersonOid should not be null", application.getPersonOid());
    }

    @Test
    public void testSetPersonNotFi() {
        Application application = new Application();
        answerMap.remove(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER);
        answerMap.put(OppijaConstants.ELEMENT_ID_NATIONALITY, "swe");
        application.addVaiheenVastaukset("henkilotiedot", answerMap);
        application = applicationPostProcessorService.addPersonOid(application);
        assertNotNull("PersonOid should not be null", application.getPersonOid());
    }
}
