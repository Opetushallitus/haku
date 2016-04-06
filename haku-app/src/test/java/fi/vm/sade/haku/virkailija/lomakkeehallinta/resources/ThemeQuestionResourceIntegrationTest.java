package fi.vm.sade.haku.virkailija.lomakkeehallinta.resources;


import fi.vm.sade.haku.oppija.hakemus.it.IntegrationTestSupport;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestionCompact;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.resources.ThemeQuestionResource;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("it")
public class ThemeQuestionResourceIntegrationTest extends IntegrationTestSupport {

    ThemeQuestionResource themeQuestionResource = appContext.getBean(ThemeQuestionResource.class);

    @Test
    public void testThemeQuestionsAsMap() {
        Response response = themeQuestionResource.getAllThemeQuestionsAsMap("1.2.246.562.29.10714450698", "fi");

        Map<ObjectId, ThemeQuestionCompact> questionMap = (Map<ObjectId, ThemeQuestionCompact>) response.getEntity();

        assertEquals(3, questionMap.size());
        assertEquals("Pohjakoulutus", questionMap.get(new ObjectId("55a78c0de4b02c5bdc839526")).getMessageText());
        assertEquals("Ennakkotehtävä", questionMap.get(new ObjectId("5576dcade4b028b630a4faec")).getMessageText());
        assertEquals("Kyllä", questionMap.get(new ObjectId("5576dcade4b028b630a4faec")).getOptions().get("option_0"));
    }

    @Test
    public void testThemeQuestionsAsMapReturnsErrorWhenMissingParameters() {
        Response response = themeQuestionResource.getAllThemeQuestionsAsMap("", "");

        Map<String, Set<String>> entity = (Map<String, Set<String>>) response.getEntity();
        Set<String> errors = entity.get("errors");

        assertEquals(2, errors.size());
        assertTrue(errors.contains("lang is required"));
        assertTrue(errors.contains("applicationSystemId is required"));
    }
}
