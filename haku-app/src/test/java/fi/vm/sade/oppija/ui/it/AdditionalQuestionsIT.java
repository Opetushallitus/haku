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

import fi.vm.sade.oppija.common.selenium.DummyModelBaseItTest;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

/**
 * @author Mikko Majapuro
 */
public class AdditionalQuestionsIT extends DummyModelBaseItTest {

    public static final String AO_ID = "1.2.246.562.14.71344129359";

    @Ignore
    @Test
    public void testAdditionalQuestion() {
        navigateToPath("lomake", ASID, "yhteishaku/hakutoiveet/hakutoiveetGrp/additionalquestions", AO_ID);
        findById(AO_ID.replace(".", "_") + "_additional_question_1");
        findByXPath("//input[@value='q1_option_1']");
        findByXPath("//input[@value='q1_option_2']");
        findByXPath("//input[@value='q1_option_3']");
    }


    @Test(expected = NoSuchElementException.class)
    public void testNoAdditionalQuestion() {
        navigateToPath("lomake", ASID, "yhteishaku/hakutoiveet/hakutoiveetGrp/additionalquestions", "6_1");
        findById("6_1_additional_question_1");
    }
}
