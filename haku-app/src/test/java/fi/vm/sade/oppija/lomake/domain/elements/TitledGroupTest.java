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

package fi.vm.sade.oppija.lomake.domain.elements;

import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TitledGroupTest {

    public static final String ID = "ID";
    public static final String TITLE = "title";
    private TitledGroup titledGroup;

    @Before
    public void setUp() throws Exception {
        titledGroup = new TitledGroup(ID, ElementUtil.createI18NForm(TITLE));
    }

    @Test
    public void testId() throws Exception {
        assertEquals(ID, titledGroup.getId());
    }

    @Test
    public void testTitle() throws Exception {
        // TODO titledGroup(Titled) paljastaa liikaa sisäistä rakennetta.
        assertEquals(TITLE+" [fi]", titledGroup.getI18nText().getTranslations().get("fi"));
    }
}
