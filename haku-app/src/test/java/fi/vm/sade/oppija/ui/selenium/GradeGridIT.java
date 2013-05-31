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
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.GradesTable;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertNotNull;

/**
 * @author Hannu Lyytikainen
 */
public class GradeGridIT extends AbstractSeleniumBase {

    public static final String PHASE_ID = "arvosanat";
    public static final String FORM_ID = "lomake";

    @Before
    public void init() {
        super.before();
        ApplicationPeriod applicationPeriod = new ApplicationPeriod(ASID);
        Phase arvosanat = new Phase(PHASE_ID, createI18NAsIs("Arvosanat"), false);
        Form form = new Form(FORM_ID, createI18NAsIs("yhteishaku"));
        form.addChild(arvosanat);
        KoodistoServiceMockImpl koodistoService = new KoodistoServiceMockImpl();
        GradesTable gradesTable = new GradesTable(koodistoService, true);
        arvosanat.addChild(gradesTable.createGradeGrid("id"));
        applicationPeriod.addForm(form);

        FormModel formModel = new FormModel();
        formModel.addApplicationPeriod(applicationPeriod);
        updateIndexAndFormModel(formModel);
    }

    @Test
    public void testTableExists() {
        final String url = getBaseUrl() + "lomake/" + ASID + "/lomake/arvosanat";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(url);

        assertNotNull(driver.findElement(By.id("gradegrid-table")));

    }

    @Test
    public void testAddLanguage() {
        final String url = getBaseUrl() + "lomake/" + ASID + "/lomake/arvosanat";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(url);

        driver.findElement(By.id("languages")).click();
        driver.findElement(By.id("nativeLanguage")).click();

        assertNotNull(driver.findElement(By.id("custom-grades-0")));
    }
}
