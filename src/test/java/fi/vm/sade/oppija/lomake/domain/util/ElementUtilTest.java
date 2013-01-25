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

import fi.vm.sade.oppija.lomake.domain.I18nText;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ElementUtilTest {

    public static final String TEST_TEXT = "test";

    @Test
    public void testCreateI18NTextSize() throws Exception {
        I18nText test = ElementUtil.createI18NText("test");
        assertTrue(test.getTranslations().size() == 3);
    }

    @Test
    public void testCreateI18NTextFi() throws Exception {
        I18nText test = ElementUtil.createI18NText(TEST_TEXT);
        assertEquals(test.getTranslations().get("fi"), TEST_TEXT);
    }
}
