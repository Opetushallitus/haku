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
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.io.IOException;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;

public class DropdownSelectHAK_295 extends AbstractSeleniumBase {

    public static final String SELECT_ID = "select_id";
    public static final String OPTION_1_ID = "FI";
    public static final String OPTION_2_ID = "SV";
    public static final String OPTION_3_ID = "xx";
    public static final String FI_VM_SADE_OPPIJA_LANGUAGE = "fi_vm_sade_oppija_language";
    public static final String SELECTED_ATTRIBUTE = "selected";
    private ApplicationSystemHelper applicationSystemHelper;
    private Element dropdownSelect;
    Option option1;
    Option option2;
    Option option3;

    private void init(final String dropdownAttribute, final boolean setDefault) {
        option1 = (Option) new OptionBuilder().setValue(OPTION_1_ID).i18nText(createI18NAsIs(OPTION_1_ID)).build();
        option2 = (Option) new OptionBuilder().setValue(OPTION_2_ID).i18nText(createI18NAsIs(OPTION_2_ID)).build();
        option3 = (Option) new OptionBuilder().setValue(OPTION_3_ID).i18nText(createI18NAsIs(OPTION_3_ID)).build();
        dropdownSelect = new DropdownSelectBuilder(SELECT_ID)
                .addOptions(ImmutableList.of(option3, option2, option1))
                .defaultOption(dropdownAttribute)
                .i18nText(createI18NAsIs(SELECT_ID)).build();
        if (setDefault) {
            option2.setDefaultOption(true);
        }
        ApplicationSystem applicationSystem = new FormModelBuilder().buildDefaultFormWithFields(dropdownSelect);
        this.applicationSystemHelper = updateApplicationSystem(applicationSystem);
        seleniumContainer.getDriver().get(getBaseUrl() + this.applicationSystemHelper.getFormUrl(this.applicationSystemHelper.getFirstPhase().getId()));
    }

    @Test
    public void testSelectWithDefault() throws IOException {
        init(null, true);
        assertOption(option1, null);
        assertOption(option2, "true");
        assertOption(option3, null);
    }

    @Test
    public void testSelect() throws IOException {
        init(null, false);
        assertOption(option1, "true");
        assertOption(option2, null);
        assertOption(option3, null);
    }

    @Ignore
    @Test
    public void testSelectWithAttribute() throws IOException {
        init(FI_VM_SADE_OPPIJA_LANGUAGE, false);
        assertOption(option2, null);
        assertOption(option3, null);
        assertOption(option1, "true");
        seleniumContainer.getDriver().get(getBaseUrl() + this.applicationSystemHelper.getFormUrl(this.applicationSystemHelper.getFirstPhase().getId()) + "?lang=sv");
        assertOption(option1, null);
        assertOption(option2, null);
        assertOption(option2, "true");
    }

    @Test
    public void testSelectWithAttributeAndDefault() throws IOException {
        init(FI_VM_SADE_OPPIJA_LANGUAGE, true);
        assertOption(option1, "true");
        assertOption(option2, null);
        assertOption(option3, null);
    }

    private void assertOption(final Option option, String value) {
        WebElement element = findByXPath("//select['@name" + dropdownSelect.getId() + "']/option[@value='" + option.getValue() + "']");
        assertEquals("Selected attribute not found (" + SELECT_ID + ")", value, element.getAttribute(SELECTED_ATTRIBUTE));
    }
}
