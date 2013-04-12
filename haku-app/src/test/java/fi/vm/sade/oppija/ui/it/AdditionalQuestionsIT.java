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

package fi.vm.sade.oppija.ui.it;

import fi.vm.sade.oppija.common.it.AbstractFormTest;
import fi.vm.sade.oppija.lomake.Yhteishaku2013;
import fi.vm.sade.oppija.lomake.dao.impl.FormServiceMockImpl;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import org.junit.Before;
import org.junit.Test;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author Mikko Majapuro
 */
public class AdditionalQuestionsIT extends AbstractFormTest {

    public static final String AO_ID = "1.2.246.562.14.71344129359";

    @Before
    public void init() {
        FormServiceMockImpl dummyMem = new FormServiceMockImpl();
        FormModel formModel = dummyMem.getModel();
        updateModelAndCreateFormModelHelper(formModel);
    }

    @Test
    public void testAdditionalQuestion() {
        beginAt("lomake/" + Yhteishaku2013.ASID + "/yhteishaku/hakutoiveet/hakutoiveetGrp/additionalquestions/" + AO_ID);

        assertFormElementPresent(AO_ID.replace(".", "_") + "_additional_question_1");
        assertElementPresentByXPath("//input[@value='q1_option_1']");
        assertElementPresentByXPath("//input[@value='q1_option_2']");
        assertElementPresentByXPath("//input[@value='q1_option_3']");
    }

    @Test
    public void testNoAdditionalQuestion() {
        beginAt("lomake/" + Yhteishaku2013.ASID + "/yhteishaku/hakutoiveet/hakutoiveetGrp/additionalquestions/6_1");
        assertFormElementNotPresent("6_1_additional_question_1");
        assertElementNotPresentByXPath("//input[@value='q2_option_1']");
        assertElementNotPresentByXPath("//input[@value='q2_option_2']");
        assertFormElementNotPresent("6_1_additional_question_2");
    }
}
