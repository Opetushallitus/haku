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
import fi.vm.sade.oppija.lomake.domain.elements.Group;
import fi.vm.sade.oppija.lomake.domain.elements.Text;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import net.sourceforge.jwebunit.api.IElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;
import static junit.framework.Assert.assertNotNull;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.getElementByXPath;

public class GroupIT extends AbstractFormTest {
    public static final String GROUP_ID = "grpid";
    public static final Text CHILD_ELEMENT = new Text("textId", ElementUtil.createI18NText("text"));
    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        Group group = new Group(GROUP_ID, createI18NText("foo"));
        group.addChild(CHILD_ELEMENT);
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(group);
        this.formModelHelper = updateModelAndCreateFormModelHelper(formModel);
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
    }

    @Test
    public void testFieldSet() throws IOException {
        locateElementByXPath("//fieldset[@class='form-item']");
    }

    @Test
    public void testLegend() throws Exception {
        locateElementByXPath("//legend[@class='form-item-label']");
    }

    @Test
    public void testContainerDiv() throws Exception {
        locateElementByXPath("//div[@class='form-item-content']");
    }

    private void locateElementByXPath(final String xpath) {
        final IElement elementById = getElementByXPath(xpath);
        assertNotNull("Unable to locate element by xpath " + xpath, elementById);
    }
}
