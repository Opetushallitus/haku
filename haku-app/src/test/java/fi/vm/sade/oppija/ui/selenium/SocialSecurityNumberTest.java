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

package fi.vm.sade.oppija.ui.selenium;

import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.lomake.FormModelHelper;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import java.io.IOException;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.*;


/**
 * @author jukka
 * @version 10/3/123:25 PM}
 * @since 1.1
 */
public class SocialSecurityNumberTest extends AbstractSeleniumBase {
    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {

        TextQuestion henkilötunnus = new TextQuestion("Henkilotunnus", createI18NAsIs("Henkilotunnus"));
        henkilötunnus.addAttribute("placeholder", "ppkkvv*****");
        henkilötunnus.addAttribute("title", "ppkkvv*****");
        addRequiredValidator(henkilötunnus);
        henkilötunnus.setValidator(createRegexValidator(henkilötunnus.getId(), "[0-9]{6}.[0-9]{4}"));
        henkilötunnus.addAttribute("size", "11");
        henkilötunnus.addAttribute("maxlength", "11");
        henkilötunnus.setHelp(createI18NAsIs("Jos sinulla ei ole suomalaista henkilötunnusta, täytä tähän syntymäaikasi"));
        henkilötunnus.setInline(true);

        Radio sukupuoli = new Radio("Sukupuoli", createI18NAsIs("Sukupuoli"));
        sukupuoli.addOption("1", createI18NForm("form.henkilotiedot.sukupuoli.mies"), "1");
        sukupuoli.addOption("2", createI18NForm("form.henkilotiedot.sukupuoli.nainen"), "2");
        addRequiredValidator(sukupuoli);
        sukupuoli.setInline(true);

        SocialSecurityNumber socialSecurityNumber = new SocialSecurityNumber("ssn_question", createI18NAsIs("Henkilötunnus"),
                sukupuoli.getI18nText(), sukupuoli.getOptions().get(0), sukupuoli.getOptions().get(1), sukupuoli.getId(), henkilötunnus);

        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(socialSecurityNumber);
        this.formModelHelper = updateIndexAndFormModel(formModel);
        seleniumHelper.getDriver().get(getBaseUrl() + formModelHelper.getStartUrl());
    }


    @Test
    public void testInputMale() {
        seleniumHelper.getSelenium().type("Henkilotunnus", "010101-111X");
        screenshot("mies");
        seleniumHelper.getDriver().findElement(By.xpath("//*[text()='Mies']"));
    }

    @Test
    public void testInputFemale() {
        seleniumHelper.getSelenium().type("Henkilotunnus", "010101-112X");
        screenshot("nainen");
        seleniumHelper.getDriver().findElement(By.xpath("//*[text()='Nainen']"));


    }
}
