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

package fi.vm.sade.oppija.lomake.domain.util;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextArea;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ElementUtilTest {

    public static final String NO_TRANSLATION = "test";
    public static final String HAS_TRANSLATION = "translated_key";
    public static final String TRANSLATED_FI = "suomeksi";
    public static final String TRANSLATED_EN = "in english";

    @Test
    public void testCreateI18NTextSize() throws Exception {
        I18nText test = ElementUtil.createI18NText("test");
        assertTrue(test.getTranslations().size() == 3);
    }

    @Test
    public void testCreateI18NTextNoTranslation() throws Exception {
        I18nText test = ElementUtil.createI18NText(NO_TRANSLATION);
        assertEquals(NO_TRANSLATION+"[fi]", test.getTranslations().get("fi"));
    }

    @Test
    public void testCreateI18NTextHasTranslation() throws Exception {
        I18nText test = ElementUtil.createI18NText(HAS_TRANSLATION);
        assertEquals(TRANSLATED_FI, test.getTranslations().get("fi"));
        assertEquals(TRANSLATED_EN, test.getTranslations().get("en"));
    }

    @Test
    public void  testFindElementsByType() {
        Form form = new Form("form", new I18nText("form", ImmutableMap.of("fi", "form")));
        Phase phase = new Phase("phase", new I18nText("phase", ImmutableMap.of("fi", "phase")), false);
        Theme theme = new Theme("theme", new I18nText("theme", ImmutableMap.of("fi", "theme")), null);
        phase.addChild(theme);
        TextQuestion tq1 = new TextQuestion("text1", new I18nText("text1", ImmutableMap.of("fi", "text1")));
        TextQuestion tq2 = new TextQuestion("text2", new I18nText("text2", ImmutableMap.of("fi", "text2")));
        TextArea ta = new TextArea("textarea", new I18nText("textarea", ImmutableMap.of("fi", "textarea")));
        theme.addChild(tq1);
        theme.addChild(ta);
        theme.addChild(tq2);
        form.addChild(phase);
        Map<String, TextQuestion> result = ElementUtil.<TextQuestion>findElementsByType(form, TextQuestion.class);
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
