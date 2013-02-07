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
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomake.Yhteishaku2013;
import fi.vm.sade.oppija.lomake.domain.AnonymousUser;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import fi.vm.sade.oppija.lomake.validation.Validator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FormServiceImplTest {

    public static final Form FORM = new Form("FormId", createI18NText("Form title"));
    public static final Phase PHASE = new Phase("phaseId", createI18NText("Phase title"), false);
    public ApplicationPeriod applicationPeriod;
    private FormServiceImpl formService;

    @Before
    public void setUp() throws Exception {
        this.applicationPeriod = new ApplicationPeriod("ApplicationPeriodId");
        FormModelHolder holder = new FormModelHolder(new Yhteishaku2013(new KoodistoServiceMockImpl()));
        FormModel model = new FormModel();
        applicationPeriod.addForm(FORM);
        FORM.addChild(PHASE);
        model.addApplicationPeriod(applicationPeriod);
        holder.updateModel(model);
        formService = new FormServiceImpl(holder);
        FORM.init();
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

    @Test
    public void testGetVaiheValidators() throws Exception {
        FormId formId = new FormId(applicationPeriod.getId(), FORM.getId());
        Application application = new Application(formId, new AnonymousUser());
        List<Validator> listOfValidators = formService.getVaiheValidators(new ApplicationState(application, PHASE.getId()));
        assertTrue(listOfValidators.isEmpty());
    }

    @Test(expected = ResourceNotFoundExceptionRuntime.class)
    public void testGetVaiheValidatorsPhaseNotFound() throws Exception {
        FormId formId = new FormId(applicationPeriod.getId(), FORM.getId());
        Application application = new Application(formId, new AnonymousUser());
        List<Validator> listOfValidators = formService.getVaiheValidators(new ApplicationState(application, "randomid"));
        assertTrue(listOfValidators.isEmpty());
    }
}
