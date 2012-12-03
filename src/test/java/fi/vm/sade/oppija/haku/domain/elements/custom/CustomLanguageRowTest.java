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

package fi.vm.sade.oppija.haku.domain.elements.custom;

import fi.vm.sade.oppija.lomake.domain.elements.custom.CustomLanguageRow;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomLanguageRowTest {
    public static final String OPTION_ID = "id";
    public static final String ROW_ID = "id";
    public static final String VALUE = "value";
    public static final String TITLE = "title";
    public static final ArrayList<Option> SCOPE_OPTIONS = new ArrayList<Option>();
    private CustomLanguageRow customLanguageRow;

    @Before
    public void setUp() throws Exception {
        customLanguageRow = new CustomLanguageRow(ROW_ID, TITLE, SCOPE_OPTIONS);
    }

    @Test
    public void testAddScopeOption() throws Exception {
        customLanguageRow.addScopeOption(OPTION_ID, VALUE, TITLE);
        assertTrue(customLanguageRow.getScopeOptions().size() == 1);
    }

    @Test
    public void testGetScopeOptions() throws Exception {
        customLanguageRow.addScopeOption(OPTION_ID, VALUE, TITLE);
        assertEquals(ROW_ID + CustomLanguageRow.ID_DELIMITER + OPTION_ID, customLanguageRow.getScopeOptions().get(0).getId());
    }
}
