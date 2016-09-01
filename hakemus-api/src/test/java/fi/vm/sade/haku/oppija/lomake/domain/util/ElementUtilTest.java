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

package fi.vm.sade.haku.oppija.lomake.domain.util;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextAreaBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.ThemeBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextArea;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ElementUtilTest {

    public static final String NO_TRANSLATION = "testjklsdhfjklsahdfkljsafhkljsafhjksadfhjksalfhkljsafhiwe";
    public static final String HAS_TRANSLATION = "form.add.lang";
    public static final String TRANSLATED_FI = "Lisää kieli";

    @Test
    public void testCreateI18NTextSize() throws Exception {
        I18nText test = ElementUtil.createI18NText(HAS_TRANSLATION);
        assertTrue(test.size() == 3);
    }

    @Test
    public void testCreateI18NTextNoTranslation() throws Exception {
        I18nText test = ElementUtil.createI18NText(NO_TRANSLATION);
        assertEquals(null, test.getTextOrNull("fi"));
    }

    @Test
    public void testCreateI18NTextHasTranslation() throws Exception {
        I18nText test = ElementUtil.createI18NText(HAS_TRANSLATION);
        assertEquals(TRANSLATED_FI, test.getTextOrNull("fi"));
    }

    @Test
    public void testFindElementsByTypeAsList() {
        Form form = new Form("form", new I18nText(ImmutableMap.of("fi", "form")));
        Element phase = new PhaseBuilder("phase").setEditAllowedByRoles("TESTING")
                .i18nText(new I18nText(ImmutableMap.of("fi", "phase"))).build();
        Element theme = new ThemeBuilder("theme").previewable().i18nText(new I18nText(ImmutableMap.of("fi", "theme"))).build();
        phase.addChild(theme);
        TextQuestion tq1 = (TextQuestion) new TextQuestionBuilder("text1").i18nText(new I18nText(ImmutableMap.of("fi", "text1"))).build();
        TextQuestion tq2 = (TextQuestion) new TextQuestionBuilder("text2").i18nText(new I18nText(ImmutableMap.of("fi", "text2"))).build();
        TextArea ta = (TextArea) TextAreaBuilder.TextArea("textarea").i18nText(new I18nText(ImmutableMap.of("fi", "textarea"))).build();
        theme.addChild(tq1);
        theme.addChild(ta);
        theme.addChild(tq2);
        form.addChild(phase);
        List<TextQuestion> result = ElementUtil.findElementsByTypeAsList(form, TextQuestion.class);
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
