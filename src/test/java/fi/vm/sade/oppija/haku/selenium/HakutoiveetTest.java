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

package fi.vm.sade.oppija.haku.selenium;

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.Phase;
import fi.vm.sade.oppija.haku.domain.elements.Theme;
import fi.vm.sade.oppija.haku.domain.elements.custom.PreferenceRow;
import fi.vm.sade.oppija.haku.domain.elements.custom.SortableTable;
import fi.vm.sade.oppija.haku.domain.elements.questions.Question;
import fi.vm.sade.oppija.haku.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.haku.domain.rules.RelatedQuestionRule;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNull;

/**
 * Test for education institute preferences
 *
 * @author Mikko Majapuro
 */
public class HakutoiveetTest extends AbstractSeleniumBase {

    @Before
    public void init() throws IOException {
        super.before();
        ApplicationPeriod applicationPeriod = new ApplicationPeriod("Yhteishaku");
        FormModel formModel = new FormModel();
        formModel.addApplicationPeriod(applicationPeriod);
        Phase hakutoiveet = new Phase("hakutoiveet", "Hakutoiveet", false);
        Phase lisakysymykset = new Phase("lisakysymykset", "Lisäkysymykset", false);
        Form form = new Form("lomake", "yhteishaku");
        form.addChild(hakutoiveet);
        form.addChild(lisakysymykset);
        form.init();

        Map<String, List<Question>> lisakysymysMap = new HashMap<String, List<Question>>();

        TextQuestion textQuestion = new TextQuestion("S1598_additional_question_1", "Lorem ipsum");
        List<Question> lisakysymysList = new ArrayList<Question>();
        lisakysymysList.add(textQuestion);
        lisakysymysMap.put("S1508", lisakysymysList);
        applicationPeriod.addForm(form);

        Theme hakutoiveetRyhmä = new Theme("hakutoiveetGrp", "Hakutoiveet", lisakysymysMap);
        hakutoiveet.addChild(hakutoiveetRyhmä);
        hakutoiveetRyhmä.setHelp("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.");
        SortableTable sortableTable = new SortableTable("preferencelist", "Hakutoiveet", "Ylös", "Alas");
        PreferenceRow pr1 = new PreferenceRow("preference1", "Hakutoive 1", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        PreferenceRow pr2 = new PreferenceRow("preference2", "Hakutoive 2", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        PreferenceRow pr3 = new PreferenceRow("preference3", "Hakutoive 3", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        sortableTable.addChild(pr1);
        sortableTable.addChild(pr2);
        sortableTable.addChild(pr3);
        hakutoiveetRyhmä.addChild(sortableTable);

        TextQuestion lisakysymys = new TextQuestion("lisakysymys", "Lisäkysymys");
        Theme lisakysymyksetRyhma = new Theme("lisakysymyksetGrp", "Lisäkysymykset", null);
        lisakysymykset.addChild(lisakysymyksetRyhma);
        RelatedQuestionRule relatedQuestionRule = new RelatedQuestionRule("rule1", "preference1-Koulutus-id", "S1598");
        relatedQuestionRule.addChild(lisakysymys);
        lisakysymyksetRyhma.addChild(relatedQuestionRule);

        initModel(formModel);
        final fi.vm.sade.oppija.common.selenium.AdminEditPage adminEditPage = new fi.vm.sade.oppija.common.selenium.AdminEditPage(getBaseUrl(), seleniumHelper);
        seleniumHelper.navigate(adminEditPage);
        adminEditPage.login("admin");
        seleniumHelper.getDriver().get(getBaseUrl() + "/admin/index/update");
    }

    @Test
    public void testEducationPreference() throws InterruptedException {
        final String url = "lomake/Yhteishaku/lomake/hakutoiveet";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + "/" + url);
        driver.findElement(By.id("preference1-Opetuspiste"));
        Selenium s = seleniumHelper.getSelenium();
        s.typeKeys("preference1-Opetuspiste", "Hel");
        driver.findElement(By.linkText("Helsingin sosiaali- ja terveysalan oppilaitos, Laakson koulutusyksikkö")).click();
        driver.findElement(By.xpath("//option[@value='Sosiaali- ja terveysalan perustutkinto, pk']")).click();
        //driver.findElement(By.id("P1_additional_question_1"));
    }

    @Test
    public void testEducationPreferenceAdditionalQuestion() throws InterruptedException {
        testEducationPreference();
        final WebDriver driver = seleniumHelper.getDriver();
        driver.findElement(By.xpath("//button[@class='right']")).click();
        driver.findElement(By.id("lisakysymys"));
    }

    @Test(expected = NoSuchElementException.class)
    public void testEducationPreferenceNoAdditionalQuestion() throws InterruptedException {
        final String url = "lomake/Yhteishaku/lomake/hakutoiveet";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + "/" + url);
        Selenium s = seleniumHelper.getSelenium();
        s.typeKeys("preference1-Opetuspiste", "Hel");
        driver.findElement(By.linkText("Helsingin sosiaali- ja terveysalan oppilaitos, Laakson koulutusyksikkö")).click();
        driver.findElement(By.xpath("//option[@value='Ensihoidon koulutusohjelma, yo']")).click();
        driver.findElement(By.xpath("//button[@name='nav-next']")).click();
        assertNull(driver.findElement(By.id("lisakysymys")));
    }
}
