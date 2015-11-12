package fi.vm.sade.haku.oppija.hakemus.it.validators;

import com.google.common.collect.Lists;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.IntegrationTestSupport;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.service.Session;
import fi.vm.sade.haku.oppija.ui.controller.FormController;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.servlet.jsp.jstl.core.Config;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;

import static junit.framework.Assert.*;

@ActiveProfiles("it")
public class EmailUniqueValidatorTest extends IntegrationTestSupport {

    private FormController formController = appContext.getBean(FormController.class);
    private MockHttpServletRequest request;
    private static final String APPLICATION_SYSTEM_ID = "1.2.246.562.29.95390561488";
    private MultivaluedMap answers = new MultivaluedMapImpl();

    @Before
    public void before() {
        ApplicationService applicationService = appContext.getBean(ApplicationService.class);

        Session session = Mockito.mock(Session.class);
        User mockUser = new User("mockUser");
        Mockito.when(session.getUser()).thenReturn(mockUser);
        Application application = new Application(APPLICATION_SYSTEM_ID, mockUser);
        application.setPhaseId("henkilotiedot");
        Mockito.when(session.getApplication(APPLICATION_SYSTEM_ID)).thenReturn(application);

        Whitebox.setInternalState(applicationService, "userSession", session);
        request = new MockHttpServletRequest();
        request.getSession().setAttribute(Config.FMT_LOCALE + ".session", new Locale("fi"));


        answers.put("phaseId", Lists.newArrayList("koulutustausta"));
        answers.put("huoltajannimi", Lists.newArrayList(""));
        answers.put("onkosinullakaksoiskansallisuus", Lists.newArrayList("false"));
        answers.put("kansalaisuus", Lists.newArrayList("FIN"));
        answers.put("asuinmaa", Lists.newArrayList("FIN"));
        answers.put("Sukunimi", Lists.newArrayList("Meikäläinen"));
        answers.put("matkapuhelinnumero1", Lists.newArrayList(""));
        answers.put("Henkilotunnus", Lists.newArrayList("111195-9493"));
        answers.put("huoltajansahkoposti", Lists.newArrayList(""));
        answers.put("Postinumero", Lists.newArrayList("00100"));
        answers.put("lahiosoite", Lists.newArrayList("Testikatu 1"));
        answers.put("Etunimet", Lists.newArrayList("Matti Mikael"));
        answers.put("Kutsumanimi", Lists.newArrayList("Matti"));
        answers.put("kotikunta", Lists.newArrayList("892"));
        answers.put("koulusivistyskieli", Lists.newArrayList("FI"));
        answers.put("aidinkieli", Lists.newArrayList("FI"));
        answers.put("huoltajanpuhelinnumero", Lists.newArrayList(""));
    }

    @Test
    public void testThatDuplicateErrorWhenEmailIsAlreadyUsed() throws URISyntaxException {
        answers.put("Sähköposti", Lists.newArrayList("hakija-19995@oph.fi"));

        Response response = formController.savePhase(request, APPLICATION_SYSTEM_ID, "henkilotiedot", answers);
        Viewable entity = (Viewable) response.getEntity();
        Map model = (Map) entity.getModel();
        Map errorMessages = (Map) model.get("errorMessages");
        I18nText emailErrorMsg = (I18nText) errorMessages.get(OppijaConstants.ELEMENT_ID_EMAIL);
        assertTrue(emailErrorMsg.getText("fi").contains("Tällä sähköpostiosoitteella on jo jätetty"));
    }

    @Test
    public void testThatNoErrorWhenEmailIsNotUsed() throws URISyntaxException {
        answers.put("Sähköposti", Lists.newArrayList("unique-email@not-used.com"));

        Response response = formController.savePhase(request, APPLICATION_SYSTEM_ID, "henkilotiedot", answers);
        //assertNull(response.getEntity());
        URI redirectLocation = (URI) response.getMetadata().get("Location").get(0);
        assertTrue(redirectLocation.getPath().contains("/koulutustausta"));
    }

}