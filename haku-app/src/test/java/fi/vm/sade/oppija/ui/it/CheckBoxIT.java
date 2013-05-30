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
import fi.vm.sade.oppija.lomake.domain.elements.Text;
import fi.vm.sade.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import net.sourceforge.jwebunit.api.IElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class CheckBoxIT extends AbstractFormTest {
    public static final String CHECKBOX_ID = "checkbox";
    public static final Text TEXT_ELEMENT = new Text("textId", ElementUtil.createI18NAsIs("text"));
    private FormModelHelper formModelHelper;
    private CheckBox checkBox;

    @Before
    public void init() throws IOException {
        checkBox = new CheckBox(CHECKBOX_ID, createI18NAsIs("foo"));
        checkBox.addChild(TEXT_ELEMENT);
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(checkBox);
        this.formModelHelper = updateModelAndCreateFormModelHelper(formModel);

        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
    }

    @Test
    public void testInputType() throws IOException {
        final IElement elementById = getElementById(CHECKBOX_ID);
        assertEquals("checkbox", elementById.getAttribute("type"));
    }

    @Test
    public void testCheckboxFieldContainer() throws IOException {
        final IElement elementByXPath = getElementByXPath("//div[@class='field-container-checkbox']");
        assertNotNull("Xpath //div[@class='field-container-checkbox'] not found", elementByXPath);
    }

    @Test
    public void testCheckboxDivClear() throws IOException {
        final IElement elementByXPath = getElementByXPath("//div[@class='clear']");
        assertNotNull("Xpath //div[@class='clear'] not found", elementByXPath);
    }

    @Test
    public void testChild() throws Exception {
        assertNotNull("Unable to locate element with id \"textId", getElementById(TEXT_ELEMENT.getId()));
    }
}
