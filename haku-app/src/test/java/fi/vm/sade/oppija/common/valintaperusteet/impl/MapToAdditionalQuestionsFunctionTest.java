/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.oppija.common.valintaperusteet.impl;

import com.google.common.base.Charsets;
import fi.vm.sade.oppija.common.valintaperusteet.AdditionalQuestions;
import fi.vm.sade.oppija.common.valintaperusteet.InputParameter;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

public class MapToAdditionalQuestionsFunctionTest {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void test() throws JsonParseException, JsonMappingException, IOException {
        final String simple = "{\"2193293289\": {\"1\": {\"aidinkieli_yo\": \"DESIMAALILUKU\"}}}";
        final ObjectMapper mapper = new ObjectMapper();
        final Map map = mapper.readValue(new ByteArrayInputStream(simple.getBytes(Charsets.UTF_8)), Map.class);

        AdditionalQuestions q = new MapToAdditionalQuestionsFunction().apply(map);
        Assert.assertEquals(1, q.getQuestistionsForHakukohde("2193293289").size());
        Assert.assertEquals(0, q.getQuestistionsForHakukohde("000").size());
        final InputParameter param = q.getQuestistionsForHakukohde("2193293289").get(0);
        Assert.assertEquals("aidinkieli_yo", param.getKey());
        Assert.assertEquals("1", param.getPhase());
        Assert.assertEquals("DESIMAALILUKU", param.getType());

        final String complex = "{\"2193293289\": {\"1\": {\"aidinkieli_yo\": \"DESIMAALILUKU\",\"foo\": \"TOTUUSARVO\"},\"2\": {\"foobar\": \"TOTUUSARVO\"}},\"1193293289\": {\"1\": {\"aidinkieli_yo\": \"DESIMAALILUKU\",\"foo\": \"TOTUUSARVO\"}}}";
        final Map complexMap = mapper.readValue(new ByteArrayInputStream(complex.getBytes(Charsets.UTF_8)), Map.class);
        q = new MapToAdditionalQuestionsFunction().apply(complexMap);
        Assert.assertEquals(3, q.getQuestistionsForHakukohde("2193293289").size());
        Assert.assertEquals(2, q.getQuestistionsForHakukohde("1193293289").size());


    }

}
