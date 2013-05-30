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
import fi.vm.sade.oppija.lomake.dao.impl.FormServiceMockImpl;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.Yhteishaku2013;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * @author Hannu Lyytikainen
 */
public class EducationBackgroundTest extends AbstractSeleniumBase {

    private FormModelHelper formModelHelper;

    @Before
    public void init() {
        FormServiceMockImpl formModel = new FormServiceMockImpl(ASID, AOID);
        this.formModelHelper = updateIndexAndFormModel(formModel.getModel());

    }

    @Test
    public void testRule() {
        final String startUrl = formModelHelper.getFormUrl(formModelHelper.getFirstForm().getPhase("koulutustausta"));

        WebDriver driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl()  + startUrl); //  lomake/Yhteishaku/yhteishaku/henkilotiedot

        driver.findElement(new By.ById("POHJAKOULUTUS_" + Yhteishaku2013.TUTKINTO_PERUSKOULU)).click();

        driver.findElement(new By.ByName("PK_PAATTOTODISTUSVUOSI"));

        driver.findElement(new By.ById("POHJAKOULUTUS_" + Yhteishaku2013.TUTKINTO_YLIOPPILAS)).click();

        driver.findElement(new By.ByName("lukioPaattotodistusVuosi"));

    }

}
