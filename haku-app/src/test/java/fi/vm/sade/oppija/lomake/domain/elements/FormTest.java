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

package fi.vm.sade.oppija.lomake.domain.elements;

import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import org.junit.Test;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;
import static org.junit.Assert.assertEquals;

public class FormTest {

    public static final String ID_TO_GET = "id2";
    private Form form = new Form("id", createI18NText("title"));
    private TextQuestion expectedElement = new TextQuestion(ID_TO_GET, createI18NText("title2"));

    @Test
    public void testGetElementById() throws Exception {
        form.addChild(expectedElement);
        Element actualElement = form.getChildById(ID_TO_GET);
        assertEquals(expectedElement, actualElement);
    }

    @Test(expected = ResourceNotFoundExceptionRuntime.class)
    public void testGetElementByIdNotFound() throws Exception {
        form.addChild(expectedElement);
        form.getChildById(ID_TO_GET + "2");
    }
}
