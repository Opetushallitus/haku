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

package fi.vm.sade.oppija.lomake.selenium;

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.PostOffice;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.custom.PostalCode;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * @author Mikko Majapuro
 */
public class PostalCodeIT extends AbstractSeleniumBase {


    @Before
    public void init() throws IOException {
        ApplicationPeriod applicationPeriod = new ApplicationPeriod("test");
        FormModel formModel = new FormModel();
        formModel.addApplicationPeriod(applicationPeriod);
        Phase testivaihe = new Phase("testivaihe", "Testivaihe", false);
        Form form = new Form("lomake", "yhteishaku");
        form.addChild(testivaihe);
        form.init();

        applicationPeriod.addForm(form);

        Theme testiRyhma = new Theme("testiGrp", "TestiGrp", null);
        testivaihe.addChild(testiRyhma);
        Map<String, PostOffice> postOffices = new HashMap<String, PostOffice>();
        postOffices.put("00100", new PostOffice("Helsinki"));
        PostalCode postinumero = new PostalCode("postinumero", "postinumero", postOffices);
        postinumero.addAttribute("size", "5");
        postinumero.addAttribute("required", "required");
        postinumero.addAttribute("pattern", "[0-9]{5}");
        postinumero.addAttribute("title", "#####");
        postinumero.addAttribute("maxlength", "5");
        testiRyhma.addChild(postinumero);

        TextQuestion tq = new TextQuestion("foo", "bar");
        testiRyhma.addChild(tq);
        initModel(formModel);
    }

    @Test
    public void testPostalCode() throws InterruptedException {
        final String url = "lomake/test/lomake/testivaihe";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + "/" + url);
        driver.findElement(By.id("postinumero"));
        Selenium s = seleniumHelper.getSelenium();
        s.typeKeys("postinumero", "00100");
        driver.findElement(By.id("foo"));
        s.typeKeys("foo", "bar");
        assertTrue(seleniumHelper.getSelenium().isTextPresent("Helsinki"));
    }
}
