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

package fi.vm.sade.haku.oppija.ui.it;

import fi.vm.sade.haku.oppija.common.it.AbstractFormTest;
import fi.vm.sade.haku.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Text;
import fi.vm.sade.haku.oppija.lomake.domain.elements.TitledGroup;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import net.sourceforge.jwebunit.api.IElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.getElementByXPath;
import static org.junit.Assert.assertNotNull;

public class GroupIT extends AbstractFormTest {
    public static final String GROUP_ID = "grpid";
    public static final Text CHILD_ELEMENT = new Text("textId", ElementUtil.createI18NAsIs("text"));
    private ApplicationSystemHelper applicationSystemHelper;

    @Before
    public void init() throws IOException {
        TitledGroup titledGroup = (TitledGroup) TitledGroupBuilder.TitledGroup(GROUP_ID).i18nText(createI18NAsIs("foo")).build();
        titledGroup.addChild(CHILD_ELEMENT);
        ApplicationSystem applicationSystem = new FormModelBuilder().buildDefaultFormWithFields(titledGroup);
        this.applicationSystemHelper = updateModelAndCreateFormModelHelper(applicationSystem);
        final String startUrl = applicationSystemHelper.getStartUrl();
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
