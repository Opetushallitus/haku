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

package fi.vm.sade.haku.oppija.ui.selenium;

import com.google.common.collect.Lists;
import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.haku.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PreferenceTable;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.hakutoiveet.HakutoiveetPhaseYhteishakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import java.io.IOException;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createActiveApplicationSystem;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;

/**
 * Test for education institute preferences
 *
 * @author Mikko Majapuro
 */
public class HakutoiveetIT extends AbstractSeleniumBase {

    private ApplicationSystem activeApplicationSystem;

    @Before
    public void init() throws IOException {
        Form form = new Form("lomake", createI18NAsIs("yhteishaku"));
        activeApplicationSystem = createActiveApplicationSystem(ASID, form);
        Phase hakutoiveet = new Phase("hakutoiveet", createI18NAsIs("Hakutoiveet"), false,
                Lists.newArrayList("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD"));
        form.addChild(hakutoiveet);


        Theme hakutoiveetRyhmä = new Theme("hakutoiveetGrp", createI18NAsIs("Hakutoiveet"), true);
        hakutoiveet.addChild(hakutoiveetRyhmä);
        hakutoiveetRyhmä.setHelp(createI18NAsIs("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem."));
        PreferenceTable preferenceTable = new PreferenceTable("preferencelist", createI18NAsIs("Hakutoiveet"));
        PreferenceRow pr1 = HakutoiveetPhaseYhteishakuSyksy.createI18NPreferenceRow("preference1", "Hakutoive 1");
        PreferenceRow pr2 = HakutoiveetPhaseYhteishakuSyksy.createI18NPreferenceRow("preference2", "Hakutoive 2");
        PreferenceRow pr3 = HakutoiveetPhaseYhteishakuSyksy.createI18NPreferenceRow("preference3", "Hakutoive 3");
        preferenceTable.addChild(pr1);
        preferenceTable.addChild(pr2);
        preferenceTable.addChild(pr3);
        hakutoiveetRyhmä.addChild(preferenceTable);
        updateApplicationSystem(activeApplicationSystem);
    }

    @Test
    public void testEducationPreferenceAdditionalQuestion() throws InterruptedException {
        final WebDriver driver = seleniumContainer.getDriver();
        seleniumContainer.navigate(getHakutoiveetPath());
        driver.findElement(By.id("preference1-Opetuspiste"));
        Selenium s = seleniumContainer.getSelenium();
        s.typeKeys("preference1-Opetuspiste", "Esp");
        driver.findElement(By.linkText("FAKTIA, Espoo op")).click();
        driver.findElement(By.xpath("//option[@value='Kaivosalan perustutkinto, pk']")).click();
        s.isTextPresent("Kaivosalan perustutkinto, Kaivosalan koulutusohjelma");

        clickByNameAndValue("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys", "true");
        clickByNameAndValue("preference1_sora_terveys", "false");
        clickByNameAndValue("preference1_sora_oikeudenMenetys", "false");

        driver.findElement(By.xpath("//button[@class='right']")).click();
    }

    @Test(expected = NoSuchElementException.class)
    public void testEducationPreferenceNoAdditionalQuestion() throws InterruptedException {
        final WebDriver driver = seleniumContainer.getDriver();
        seleniumContainer.navigate(getHakutoiveetPath());
        Selenium s = seleniumContainer.getSelenium();
        s.typeKeys("preference1-Opetuspiste", "Eso");
        driver.findElement(By.linkText("FAKTIA, Espoo op")).click();
        driver.findElement(By.xpath("//option[@value='Kaivosalan perustutkinto, pk']")).click();
        s.isTextPresent("Kaivosalan perustutkinto, Kaivosalan koulutusohjelma");
        driver.findElement(By.xpath("//button[@name='nav-next']")).click();
    }

    private String getHakutoiveetPath() {
        String hakutoiveet = ElementUtil.getPath(this.activeApplicationSystem, "hakutoiveet");
        return "lomake/" + hakutoiveet;
    }
}
