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

package fi.vm.sade.oppija.lomake.domain;

import fi.vm.sade.oppija.lomake.FormModelHelper;
import fi.vm.sade.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import org.junit.Test;

import static fi.vm.sade.oppija.lomake.dao.impl.FormModelDummyMemoryDaoImpl.createI18NText;
import static org.junit.Assert.assertEquals;

/**
 * @author jukka
 * @version 9/14/121:50 PM}
 * @since 1.1
 */
public class FormModelFactoryTest {
    @Test
    public void testFromJSONString() throws Exception {
        final FormModel formModel = FormModelFactory.fromClassPathResource("test-data.json");
        assertEquals("yhteishaku", formModel.getApplicationPeriodById("Yhteishaku").getFormById("yhteishaku").getId());
    }

    @Test
    public void testBuilderFrom() throws Exception {
        final FormModel formModel = new FormModelBuilder().withDefaults().addChildToTeema(new TextQuestion("doo", createI18NText("foo"))).build();
        final FormModelHelper formModelHelper = new FormModelHelper(formModel);
        assertEquals("doo", formModelHelper.getFirstCategoryFirstTeemaChild().getId());
    }

    @Test
    public void testBuilderWithCategory() throws Exception {
        final Phase phase = new Phase("ekaKategoria", createI18NText("ensimmäinen kategoria"), false);
        final FormModel formModel = new FormModelBuilder(phase).withDefaults().addChildToTeema(new TextQuestion("doo", createI18NText("foo"))).build();
        assertEquals("doo", new FormModelHelper(formModel).getFirstCategoryFirstTeemaChild().getId());
    }
}
