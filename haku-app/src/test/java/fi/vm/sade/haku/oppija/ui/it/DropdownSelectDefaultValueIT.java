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

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.haku.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.builder.DropdownSelectBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.builder.OptionBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;

public class DropdownSelectDefaultValueIT extends AbstractSeleniumBase {

    public static final String OPTION_1_ID = "fi";
    public static final String OPTION_2_ID = "sv";
    public static final String OPTION_3_ID = "xx";
    private final Option option1 = (Option) new OptionBuilder().setValue(OPTION_1_ID).i18nText(createI18NAsIs(OPTION_1_ID)).build();
    private final Option option2 = (Option) new OptionBuilder().setValue(OPTION_2_ID).i18nText(createI18NAsIs(OPTION_2_ID)).build();
    private final Option option3 = (Option) new OptionBuilder().setValue(OPTION_3_ID).i18nText(createI18NAsIs(OPTION_3_ID)).build();
    private ApplicationSystemHelper applicationSystemHelper;
    private WebDriver driver;
    private Element dropdownSelect;

    @Before
    public void init() throws IOException {
        String id = ElementUtil.randomId();
        dropdownSelect = new DropdownSelectBuilder(id)
                .addOptions(ImmutableList.of(option1, option2, option3))
                .i18nText(createI18NAsIs(id))
                .build();
        option2.setDefaultOption(true);
        ApplicationSystem applicationSystem = new FormModelBuilder().buildDefaultFormWithFields(dropdownSelect);
        this.applicationSystemHelper = updateApplicationSystem(applicationSystem);
        driver = seleniumContainer.getDriver();
        driver.get(getBaseUrl() + this.applicationSystemHelper.getFormUrl(this.applicationSystemHelper.getFirstPhase().getId()));
    }

    @Test
    public void testSelect() throws IOException {
        WebElement we1 = findByXPath("//select['@name" + dropdownSelect.getId() + "']/option[@value='" + option1.getValue() + "']");
        WebElement we2 = findByXPath("//select['@name" + dropdownSelect.getId() + "']/option[@value='" + option2.getValue() + "']");
        WebElement we3 = findByXPath("//select['@name" + dropdownSelect.getId() + "']/option[@value='" + option3.getValue() + "']");
        assertEquals("Invalid attribute selected", null, we1.getAttribute("selected"));
        assertEquals("Invalid attribute selected", null, we3.getAttribute("selected"));
        assertEquals("Selected attribute not present on a selected option", "true", we2.getAttribute("selected"));
    }
}
