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
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PreferenceTable;
import net.sourceforge.jwebunit.api.IElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.assertEquals;

/**
 * Preference table integration tests
 *
 * @author Mikko Majapuro
 */
public class PreferenceTableIT extends AbstractFormTest {

    private ApplicationSystemHelper applicationSystemHelper;

    @Before
    public void init() throws IOException {
        final PreferenceTable table = new PreferenceTable("t1", createI18NAsIs("Hakutoiveet"), true, 2, false);
        final PreferenceRow row = new PreferenceRow("r1",  createI18NAsIs("Tyhjennä"),
                createI18NAsIs("Koulutus"), createI18NAsIs("Opetuspiste"),
                createI18NAsIs("Koulutukseen sisältyvät koulutusohjelmat"),
                createI18NAsIs("Liitteet"));
        final PreferenceRow row2 = new PreferenceRow("r2", createI18NAsIs("Tyhjennä"),
                createI18NAsIs("Koulutus"), createI18NAsIs("Opetuspiste"),
                createI18NAsIs("Koulutukseen sisältyvät koulutusohjelmat"),
                createI18NAsIs("Liitteet"));
        table.addChild(row);
        table.addChild(row2);
        ApplicationSystem applicationSystem = new FormModelBuilder().buildDefaultFormWithFields(table);
        this.applicationSystemHelper = updateModelAndCreateFormModelHelper(applicationSystem);
    }

    @Test
    public void testSelectInputExists() throws IOException {
        final String startUrl = applicationSystemHelper.getStartUrl();
        beginAt(startUrl);
        assertElementPresent("r1-Koulutus");
        getElementById("r1-Koulutus");
        getElementById("r1-Koulutus");

        assertElementPresent("r2-Koulutus");
        getElementById("r2-Koulutus");
    }

    @Test
    public void testTextInputExists() throws IOException {
        final String startUrl = applicationSystemHelper.getStartUrl();
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

    @Test
    public void testUsePriorities() {
        final String startUrl = applicationSystemHelper.getStartUrl();
        beginAt(startUrl);
        assertElementPresentByXPath("//button[@class='down sort']");
        assertElementPresentByXPath("//button[@class='up sort']");
    }

    @Test
    public void testNotUsePriorities() {
        final PreferenceTable table = new PreferenceTable("t1", createI18NAsIs("Hakutoiveet"), false, 2, false);
        final PreferenceRow row = new PreferenceRow("r1",  createI18NAsIs("Tyhjennä"),
                createI18NAsIs("Koulutus"), createI18NAsIs("Opetuspiste"),
                createI18NAsIs("Koulutukseen sisältyvät koulutusohjelmat"),
                createI18NAsIs("Liitteet"));
        final PreferenceRow row2 = new PreferenceRow("r2", createI18NAsIs("Tyhjennä"),
                createI18NAsIs("Koulutus"), createI18NAsIs("Opetuspiste"),
                createI18NAsIs("Koulutukseen sisältyvät koulutusohjelmat"),
                createI18NAsIs("Liitteet"));
        table.addChild(row);
        table.addChild(row2);
        ApplicationSystem applicationSystem = new FormModelBuilder().buildDefaultFormWithFields(table);
        this.applicationSystemHelper = updateModelAndCreateFormModelHelper(applicationSystem);
        final String startUrl = applicationSystemHelper.getStartUrl();
        beginAt(startUrl);
        assertElementNotPresentByXPath("//button[@class='down sort']");
        assertElementNotPresentByXPath("//button[@class='up sort']");
    }
}
