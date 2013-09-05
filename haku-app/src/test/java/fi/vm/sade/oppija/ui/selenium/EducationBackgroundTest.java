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

import fi.vm.sade.oppija.common.koodisto.impl.KoodistoServiceMockImpl;
import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.FormGeneratorMock;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.koulutustausta.KoulutustaustaPhase;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class EducationBackgroundTest extends AbstractSeleniumBase {

    private ApplicationSystemHelper applicationSystemHelper;

    @Before
    public void init() {
        FormGeneratorMock formGeneratorMock = new FormGeneratorMock(new KoodistoServiceMockImpl(), ASID);
        List<ApplicationSystem> applicationSystems = formGeneratorMock.generate();
        this.applicationSystemHelper = updateApplicationSystem(applicationSystems.get(0));

    }

    @Test
    public void testRule() {
        final String startUrl = applicationSystemHelper.getFormUrl("koulutustausta");

        WebDriver driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + startUrl); //  lomake/Yhteishaku/henkilotiedot

        driver.findElement(new By.ById("POHJAKOULUTUS_" + KoulutustaustaPhase.TUTKINTO_PERUSKOULU)).click();

        driver.findElement(new By.ByName("PK_PAATTOTODISTUSVUOSI"));

        driver.findElement(new By.ById("POHJAKOULUTUS_" + KoulutustaustaPhase.TUTKINTO_YLIOPPILAS)).click();

        driver.findElement(new By.ByName("lukioPaattotodistusVuosi"));

    }

}
