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

import fi.vm.sade.oppija.lomake.FormModelHelper;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import net.sourceforge.jwebunit.api.IElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author jukka
 * @version 9/18/128:54 AM}
 * @since 1.1
 */
public class CheckBoxIT extends AbstractRemoteTest {
    private FormModelHelper formModelHelper;
    private CheckBox checkBox;

    @Before
    public void init() throws IOException {
        checkBox = new CheckBox("checkbox", "foo");
        checkBox.addOption("checkbox_value", "value", "title");
        checkBox.addOption("checkbox_value2", "value2", "title2");
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(checkBox);
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testInputExists() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        List<Option> options = checkBox.getOptions();
        for (Option option : options) {
            assertElementPresent(option.getId());
        }
        final IElement elementById = getElementById("checkbox_checkbox_value");
        assertEquals("input", elementById.getName());
        assertEquals("checkbox_checkbox_value", elementById.getAttribute("name"));
    }
}
