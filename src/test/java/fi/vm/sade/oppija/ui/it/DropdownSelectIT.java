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

package fi.vm.sade.oppija.ui.it;

import fi.vm.sade.oppija.common.it.AbstractFormTest;
import fi.vm.sade.oppija.lomake.FormModelHelper;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DropdownSelect;
import net.sourceforge.jwebunit.api.IElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;
import static junit.framework.Assert.assertEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class DropdownSelectIT extends AbstractFormTest {

    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        final DropdownSelect select = new DropdownSelect("select", createI18NText("foo"));
        select.addOption("value1", createI18NText("title"), "select");
        select.addOption("value2", createI18NText("title2"), "select2");
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(select);
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testInputExists() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        assertElementPresent("select");
        final IElement select = getElementById("select");
        assertEquals(2, select.getChildren().size());
        assertEquals("select", select.getName());
        final IElement elementById = getElementById("select_value1");
        assertEquals("option", elementById.getName());
    }
}
