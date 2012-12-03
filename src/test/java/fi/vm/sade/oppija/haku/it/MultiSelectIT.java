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

package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.lomake.domain.elements.questions.MultiSelect;
import net.sourceforge.jwebunit.api.IElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author jukka
 * @version 9/18/122:03 PM}
 * @since 1.1
 */
public class MultiSelectIT extends AbstractRemoteTest {

    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        final MultiSelect multiSelect = new MultiSelect("multiselect", "foo");
        multiSelect.addOption("value1", "multiselect", "title");
        multiSelect.addOption("value2", "multiselect2", "title2");
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(multiSelect);
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testInputExists() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        assertElementPresent("multiselect");
        final IElement multiselect = getElementById("multiselect");
        assertEquals(2, multiselect.getChildren().size());
        assertEquals("select", multiselect.getName());
        final IElement elementById = getElementById("multiselect_value1");
        assertEquals("option", elementById.getName());
    }
}
