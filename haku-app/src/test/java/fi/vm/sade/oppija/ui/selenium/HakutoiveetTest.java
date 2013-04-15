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

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NForm;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import com.thoughtworks.selenium.Selenium;

import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.lomake.Yhteishaku2013;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.oppija.lomake.domain.elements.custom.PreferenceTable;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;

/**
 * Test for education institute preferences
 *
 * @author Mikko Majapuro
 */
public class HakutoiveetTest extends AbstractSeleniumBase {

    @Before
    public void init() throws IOException {
        super.before();
        FormModel formModel = new FormModel();
        ApplicationPeriod applicationPeriod = new ApplicationPeriod(Yhteishaku2013.ASID);
        formModel.addApplicationPeriod(applicationPeriod);
        Form form = new Form("lomake", createI18NForm("yhteishaku"));
        Phase hakutoiveet = new Phase("hakutoiveet", createI18NForm("Hakutoiveet"), false);
        Phase lisakysymykset = new Phase("lisakysymykset", createI18NForm("Lisäkysymykset"), false);
        form.addChild(hakutoiveet);
        form.addChild(lisakysymykset);

        Map<String, List<Question>> lisakysymysMap = new HashMap<String, List<Question>>();

        TextQuestion textQuestion = new TextQuestion("1_2_246_562_14_79893512065_additional_question_1", createI18NForm("Lorem ipsum"));
        List<Question> lisakysymysList = new ArrayList<Question>();
        lisakysymysList.add(textQuestion);
        lisakysymysMap.put("1.2.246.562.14.79893512065", lisakysymysList);

        Theme hakutoiveetRyhmä = new Theme("hakutoiveetGrp", createI18NForm("Hakutoiveet"), lisakysymysMap);
        hakutoiveet.addChild(hakutoiveetRyhmä);
        hakutoiveetRyhmä.setHelp(createI18NForm("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem."));
        PreferenceTable preferenceTable = new PreferenceTable("preferencelist", createI18NForm("Hakutoiveet"), "Ylös", "Alas", 32, null, null);
        PreferenceRow pr1 = ElementUtil.createI18NPreferenceRow("preference1", "Hakutoive 1");
        PreferenceRow pr2 = ElementUtil.createI18NPreferenceRow("preference2", "Hakutoive 2");
        PreferenceRow pr3 = ElementUtil.createI18NPreferenceRow("preference3", "Hakutoive 3");
        preferenceTable.addChild(pr1);
        preferenceTable.addChild(pr2);
        preferenceTable.addChild(pr3);
        hakutoiveetRyhmä.addChild(preferenceTable);

        TextQuestion lisakysymys = new TextQuestion("lisakysymys", createI18NForm("Lisäkysymys"));
        Theme lisakysymyksetRyhma = new Theme("lisakysymyksetGrp", createI18NForm("Lisäkysymykset"), null);
        lisakysymykset.addChild(lisakysymyksetRyhma);
        RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("rule1", "preference1-Koulutus-id", "1.2.246.562.14.79893512065");
        relatedQuestionRule.addChild(lisakysymys);
        lisakysymyksetRyhma.addChild(relatedQuestionRule);
        applicationPeriod.addForm(form);
        updateIndexAndFormModel(formModel);
    }

    @Test
    public void testEducationPreferenceAdditionalQuestion() throws InterruptedException {
        final WebDriver driver = seleniumHelper.getDriver();
        seleniumHelper.navigate("/lomake/" + Yhteishaku2013.ASID + "/lomake/hakutoiveet");
        driver.findElement(By.id("preference1-Opetuspiste"));
        Selenium s = seleniumHelper.getSelenium();
        s.typeKeys("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText("FAKTIA, Espoo op")).click();
        driver.findElement(By.xpath("//option[@value='Kaivosalan perustutkinto, pk']")).click();
        s.isTextPresent("Kaivosalan perustutkinto, Kaivosalan koulutusohjelma");
        driver.findElement(By.xpath("//button[@class='right']")).click();
        //seleniumHelper.navigate("/lomake/Yhteishaku/lomake/lisakysymykset");
        driver.findElement(By.id("lisakysymys"));
    }

    @Test(expected = NoSuchElementException.class)
    public void testEducationPreferenceNoAdditionalQuestion() throws InterruptedException {
        final String url = "lomake/" + Yhteishaku2013.ASID + "/lomake/hakutoiveet";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + "/" + url);
        Selenium s = seleniumHelper.getSelenium();
        s.typeKeys("preference1-Opetuspiste", "Eso");
        driver.findElement(By.linkText("FAKTIA, Espoo op")).click();
        driver.findElement(By.xpath("//option[@value='Kaivosalan perustutkinto, pk']")).click();
        s.isTextPresent("Kaivosalan perustutkinto, Kaivosalan koulutusohjelma");
        driver.findElement(By.xpath("//button[@name='nav-next']")).click();
        assertNull(driver.findElement(By.id("lisakysymys")));
    }
}
