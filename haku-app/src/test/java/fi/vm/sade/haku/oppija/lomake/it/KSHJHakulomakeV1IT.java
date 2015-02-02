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

        navigateToPath("lomake", "1.2.246.562.29.173465377510");
        fillOut(defaultValues.kkHenkilotiedot);
        elementsNotPresentById("huoltajannimi");
        elementsNotPresentById("huoltajanpuhelinnumero");
        elementsNotPresentById("huoltajansahkoposti");

        nextPhase(OppijaConstants.PHASE_EDUCATION);
        findByIdAndClick("suoritusoikeus_tai_aiempi_tutkinto", "pohjakoulutus_am");
        setValue("pohjakoulutus_am_vuosi", "2012");
        setValue("pohjakoulutus_am_nimike", "pohjakoulutus_am_nimike");
        setValue("pohjakoulutus_am_laajuus", "laajuus");
        setValue("pohjakoulutus_am_oppilaitos", "oppilaitos");
        setValue("pohjakoulutus_am_nayttotutkintona", "false");
        nextPhase(OppijaConstants.PHASE_APPLICATION_OPTIONS);

        findById("preference1-Opetuspiste");
        typeWithoutTab("preference1-Opetuspiste", "anna");
        clickLinkByText("Anna Tapion koulu");
        clickAllElementsByXPath("//option[@value='Kymppiluokka']");

        nextPhase(OppijaConstants.PHASE_GRADES);

        setValue("keskiarvo", "10,00");
        setValue("arvosanaasteikko", "4-10");
        nextPhase(OppijaConstants.PHASE_MISC);

        navigateToPath("lomake", "1.2.246.562.29.173465377510", OppijaConstants.PHASE_EDUCATION);

        findByIdAndClick("pohjakoulutus_am", "pohjakoulutus_muu");
        setValue("pohjakoulutus_muu_vuosi", "2012");
        setValue("pohjakoulutus_muu_kuvaus", "kuvaus");
        nextPhase(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        nextPhase(OppijaConstants.PHASE_GRADES);
        elementsNotPresentById("keskiarvo", "arvosanaasteikko");


        navigateToPath("lomake", "1.2.246.562.29.173465377510", OppijaConstants.PHASE_EDUCATION);
        findByIdAndClick("pohjakoulutus_yo");
        setValue("pohjakoulutus_yo_vuosi", "2002");
        setValue("pohjakoulutus_yo_tutkinto", "fi");

        nextPhase(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        nextPhase(OppijaConstants.PHASE_GRADES);
        setValue("lukion-paattotodistuksen-keskiarvo", "4,51");
        elementsNotPresentById("keskiarvo", "arvosanaasteikko");

        navigateToPath("lomake", "1.2.246.562.29.173465377510", OppijaConstants.PHASE_EDUCATION);
        findByIdAndClick("pohjakoulutus_kk_ulk");
        verifyDropdownSelection("pohjakoulutus_kk_ulk_maa", "", "");

        findByIdAndClick("pohjakoulutus_yo_ulkomainen");
        verifyDropdownSelection("pohjakoulutus_yo_ulkomainen_maa", "", "");
    }
}
