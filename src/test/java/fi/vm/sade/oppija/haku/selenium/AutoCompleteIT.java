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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.haku.selenium;

import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;

/**
 * @author Mikko Majapuro
 */

public class AutoCompleteIT extends AbstractSeleniumBase {

    @Before
    public void init() throws IOException {
        FormModel formModel = new FormModelBuilder().withDefaults().build();
        initModel(formModel);
    }

    @Test
    public void testOneMatch() throws IOException {
        seleniumHelper.getDriver().get(this.getBaseUrl() + "/education/Yhteishaku/organisaatio/search?term=Hels");
        assertFalse(seleniumHelper.getSelenium().isTextPresent("[]"));
    }

    @Test
    public void testNoMatch() throws IOException {
        seleniumHelper.getDriver().get(this.getBaseUrl() + "/education/Yhteishaku/organisaatio/search?term=xyz");
        seleniumHelper.getSelenium().isTextPresent("[]");
    }

    @Test
    public void testEmptySearchTerm() throws IOException {
        seleniumHelper.getDriver().get(this.getBaseUrl() + "/education/Yhteishaku/organisaatio/search?term=");
        seleniumHelper.getSelenium().isTextPresent("[]");
    }
}
