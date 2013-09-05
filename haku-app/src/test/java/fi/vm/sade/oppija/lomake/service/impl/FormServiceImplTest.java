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

import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.internal.matchers.Any;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FormServiceImplTest {

    public static final Form FORM = new Form(ElementUtil.randomId(), createI18NAsIs("Form title"));
    public static final Phase PHASE = new Phase(ElementUtil.randomId(), createI18NAsIs("Phase title"), false);
    private FormServiceImpl formService;

    @Before
    public void setUp() throws Exception {
        ApplicationSystem applicationSystem = ElementUtil.createActiveApplicationSystem(ElementUtil.randomId(), FORM);
        FORM.addChild(PHASE);
        ApplicationSystemService applicationSystemServiceMock = mock(ApplicationSystemService.class);
        when(applicationSystemServiceMock.getApplicationSystem(applicationSystem.getId())).thenReturn(applicationSystem);
        formService = new FormServiceImpl(applicationSystemServiceMock);
    }

    @Test(expected = ResourceNotFoundExceptionRuntime.class)
    public void testGetFirstPhaseNotFound() throws Exception {
        Element firstPhase = formService.getFirstPhase(null);
    }
}
