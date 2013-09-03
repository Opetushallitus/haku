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
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.FormGeneratorMock;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.Yhteishaku2013;
import org.junit.Before;
import org.junit.Test;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;

public class FormServiceImplTest {

    public static final Form FORM = new Form(ElementUtil.randomId(), createI18NAsIs("Form title"));
    public static final Phase PHASE = new Phase(ElementUtil.randomId(), createI18NAsIs("Phase title"), false);
    public ApplicationSystem applicationSystem;
    private FormServiceImpl formService;

    @Before
    public void setUp() throws Exception {
        this.applicationSystem = ElementUtil.createActiveApplicationSystem("ASID", FORM);
        FormGeneratorMock formGeneratorMock = new FormGeneratorMock(new KoodistoServiceMockImpl(), "ASID");
        FormModelHolder holder = new FormModelHolder(formGeneratorMock);
        FormModel model = new FormModel();
        FORM.addChild(PHASE);
        model.addApplicationSystem(applicationSystem);
        holder.updateModel(model);
        formService = new FormServiceImpl(holder);
    }

    @Test(expected = ResourceNotFoundExceptionRuntime.class)
    public void testGetFirstPhaseNotFound() throws Exception {
        formService.getFirstPhase(null);
    }

    @Test
    public void testGetApplicationSystemById() throws Exception {
        ApplicationSystem applicationSystemById = formService.getApplicationSystem(applicationSystem.getId());
        assertEquals(applicationSystem, applicationSystemById);
    }

    @Test(expected = ResourceNotFoundExceptionRuntime.class)
    public void testGetApplicationSystemByIdNotFound() throws Exception {
        formService.getApplicationSystem("lskdjflsdk");
    }
}
