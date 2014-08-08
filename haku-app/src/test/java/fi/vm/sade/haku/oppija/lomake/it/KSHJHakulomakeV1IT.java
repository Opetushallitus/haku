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

package fi.vm.sade.haku.oppija.lomake.it;

import fi.vm.sade.haku.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Test;

public class KSHJHakulomakeV1IT extends DummyModelBaseItTest {

    @Test
    public void runKSHJForm() throws Exception {

        navigateToPath("lomake", "haku6");
        fillOut(defaultValues.henkilotiedot);
        nextPhase(OppijaConstants.PHASE_EDUCATION);
        click("suoritusoikeus", "aiempitutkinto", "pohjakoulutus_am");
        setValue("pohjakoulutus_am_vuosi", "2012");
        setValue("pohjakoulutus_am_nimike", "pohjakoulutus_am_nimike");
        setValue("pohjakoulutus_am_laajuus", "laajuus");
        setValue("pohjakoulutus_am_oppilaitos", "oppilaitos");
        nextPhase(OppijaConstants.PHASE_APPLICATION_OPTIONS);

        findById("preference1-Opetuspiste");
        typeWithoutTab("preference1-Opetuspiste", "anna");
        clickLinkByText("Anna Tapion koulu");
        click("//option[@value='Kymppiluokka']");

        nextPhase(OppijaConstants.PHASE_GRADES);

        setValue("keskiarvo", "10");
        setValue("arvosanaasteikko", "4-10");
        nextPhase(OppijaConstants.PHASE_MISC);


        navigateToPath("lomake", "haku6", OppijaConstants.PHASE_EDUCATION);

        click("pohjakoulutus_am");
        click("pohjakoulutus_muu");
        setValue("pohjakoulutus_muu_vuosi", "2012");
        setValue("pohjakoulutus_muu_kuvaus", "kuvaus");
        nextPhase(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        elementsNotPresent("keskiarvo");
        elementsNotPresent("arvosanaasteikko");
    }
}
