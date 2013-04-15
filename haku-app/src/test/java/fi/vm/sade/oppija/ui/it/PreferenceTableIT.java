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
import fi.vm.sade.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.oppija.lomake.domain.elements.custom.PreferenceTable;
import net.sourceforge.jwebunit.api.IElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NForm;
import static org.junit.Assert.assertEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * Preference table integration tests
 *
 * @author Mikko Majapuro
 */
public class PreferenceTableIT extends AbstractFormTest {

    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        final PreferenceTable table = new PreferenceTable("t1", createI18NForm("Hakutoiveet"), "Ylös", "Alas");
        final PreferenceRow row = new PreferenceRow("r1", createI18NForm("Hakutoive 1"),
        		createI18NForm("Tyhjennä"), createI18NForm("Koulutus"), createI18NForm("Opetuspiste"),
        		createI18NForm("Koulutukseen sisältyvät koulutusohjelmat"), "Valitse koulutus");
        final PreferenceRow row2 = new PreferenceRow("r2", createI18NForm("Hakutoive 2"), createI18NForm("Tyhjennä"),
        		createI18NForm("Koulutus"), createI18NForm("Opetuspiste"),
        		createI18NForm("Koulutukseen sisältyvät koulutusohjelmat"), "Valitse koulutus");
        table.addChild(row);
        table.addChild(row2);
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(table);
        this.formModelHelper = updateModelAndCreateFormModelHelper(formModel);
    }

    @Test
    public void testSelectInputExists() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        assertElementPresent("r1-Koulutus");
        getElementById("r1-Koulutus");
        getElementById("r1-Koulutus");

        assertElementPresent("r2-Koulutus");
        getElementById("r2-Koulutus");
    }

    @Test
    public void testTextInputExists() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        assertElementPresent("r1-Opetuspiste");
        final IElement input = getElementById("r1-Opetuspiste");
        assertEquals("input", input.getName());
        assertEquals("text", input.getAttribute("type"));

        assertElementPresent("r2-Opetuspiste");
        final IElement input2 = getElementById("r2-Opetuspiste");
        assertEquals("input", input2.getName());
        assertEquals("text", input2.getAttribute("type"));
    }
}
