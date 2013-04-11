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

package fi.vm.sade.oppija.lomake.service.impl;

import fi.vm.sade.oppija.common.koodisto.impl.KoodistoServiceMockImpl;
import fi.vm.sade.oppija.lomake.Yhteishaku2013;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import org.junit.Before;
import org.junit.Test;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;
import static org.junit.Assert.assertEquals;

public class FormServiceImplTest {

    public static final Form FORM = new Form("FormId", createI18NText("Form title"));
    public static final Phase PHASE = new Phase("phaseId", createI18NText("Phase title"), false);
    public ApplicationPeriod applicationPeriod;
    private FormServiceImpl formService;

    @Before
    public void setUp() throws Exception {
        this.applicationPeriod = new ApplicationPeriod("ApplicationPeriodId");
        Yhteishaku2013 yhteishaku2013 = new Yhteishaku2013(new KoodistoServiceMockImpl());
        yhteishaku2013.init();
        FormModelHolder holder = new FormModelHolder(yhteishaku2013);
        FormModel model = new FormModel();
        applicationPeriod.addForm(FORM);
        FORM.addChild(PHASE);
        model.addApplicationPeriod(applicationPeriod);
        holder.updateModel(model);
        formService = new FormServiceImpl(holder);
    }

    @Test(expected = ResourceNotFoundExceptionRuntime.class)
    public void testGetFirstCategoryNotFound() throws Exception {
        formService.getFirstPhase(null, null);
    }

    @Test
    public void testGetApplicationPeriodById() throws Exception {
        ApplicationPeriod applicationPeriodById = formService.getApplicationPeriodById(applicationPeriod.getId());
        assertEquals(applicationPeriod, applicationPeriodById);
    }

    @Test(expected = ResourceNotFoundExceptionRuntime.class)
    public void testGetApplicationPeriodByIdNotFound() throws Exception {
        formService.getApplicationPeriodById("lskdjflsdk");
    }
}
