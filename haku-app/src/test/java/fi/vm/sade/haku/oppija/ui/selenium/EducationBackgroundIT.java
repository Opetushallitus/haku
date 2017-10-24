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

package fi.vm.sade.haku.oppija.ui.selenium;

import fi.vm.sade.haku.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Test;

import static fi.vm.sade.haku.oppija.ui.selenium.DefaultValues.*;

public class EducationBackgroundIT extends DummyModelBaseItTest {

    @Test
    public void testRule() {
        navigateToFirstPhase();

        fillOut(defaultValues.henkilotiedot);

        nextPhase(OppijaConstants.PHASE_EDUCATION);

        clickByNameAndValue(KYSYMYS_POHJAKOULUTUS, TUTKINTO_PERUSKOULU);
        clickByNameAndValue(KYSYMYS_POHJAKOULUTUS, TUTKINTO_YLIOPPILAS);
        elementsPresentByName(OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI);


    }
}
