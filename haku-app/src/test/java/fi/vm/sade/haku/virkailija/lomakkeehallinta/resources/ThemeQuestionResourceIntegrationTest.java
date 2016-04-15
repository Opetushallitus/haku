package fi.vm.sade.haku.virkailija.lomakkeehallinta.resources;


import com.google.common.collect.Sets;
import fi.vm.sade.haku.oppija.hakemus.it.IntegrationTestSupport;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestionCompact;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.resources.ThemeQuestionResource;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
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
    HakukohdeService hakukohdeServiceMock = appContext.getBean(HakukohdeService.class);

    @Test
    public void testThemeQuestionsAsMap() {
        Response response = themeQuestionResource.getAllThemeQuestionsAsMap("1.2.246.562.29.10714450698", "fi");

        Map<ObjectId, ThemeQuestionCompact> questionMap = (Map<ObjectId, ThemeQuestionCompact>) response.getEntity();

        assertEquals(3, questionMap.size());
        assertEquals("Pohjakoulutus", questionMap.get(new ObjectId("55a78c0de4b02c5bdc839526")).getMessageText());
        assertTrue(questionMap.get(new ObjectId("5576d69de4b0b711f6a2eb7e")).getApplicationOptionOids().contains("1.2.246.562.20.94819169833"));
        assertEquals("Ennakkotehtävä", questionMap.get(new ObjectId("5576dcade4b028b630a4faec")).getMessageText());
        assertEquals("Kyllä", questionMap.get(new ObjectId("5576dcade4b028b630a4faec")).getOptions().get("option_0"));
    }

    @Test
    public void testThatThemeQuestionAsMapReturnApplicationOptionOidsWhenQuestionIsGroup() {
        final String AS_ID = "1.2.246.562.29.75203638285";
        final String GROUP_ID = "1.2.246.562.28.62705498013";

        Response response = themeQuestionResource.getAllThemeQuestionsAsMap(AS_ID, "fi");

        Map<ObjectId, ThemeQuestionCompact> questionMap = (Map<ObjectId, ThemeQuestionCompact>) response.getEntity();

        assertEquals(
                Sets.newHashSet(hakukohdeServiceMock.findByGroupAndApplicationSystem(GROUP_ID, AS_ID)),
                questionMap.get(new ObjectId("561caf78e4b0c839a76812ff")).getApplicationOptionOids()
        );
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
