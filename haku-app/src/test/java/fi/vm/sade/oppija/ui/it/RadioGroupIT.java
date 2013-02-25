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
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertElementPresentByXPath;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;

/**
 * @author jukka
 * @version 9/18/121:44 PM}
 * @since 1.1
 */
public class RadioGroupIT extends AbstractFormTest {

    private FormModelHelper formModelHelper;
    private Radio radio;

    @Before
    public void init() throws IOException {
        radio = new Radio("radio", createI18NText("foo"));
        radio.addOption("value1", createI18NText("radio"), "title");
        radio.addOption("value2", createI18NText("radio2"), "title2");
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(radio);
        this.formModelHelper = updateModelAndCreateFormModelHelper(formModel);
    }

    @Test
    public void testInputExists() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        List<Option> options = radio.getOptions();
        for (Option option : options) {
            assertElementPresentByXPath("//input[@value='" + option.getValue() + "']");
        }
    }
}
