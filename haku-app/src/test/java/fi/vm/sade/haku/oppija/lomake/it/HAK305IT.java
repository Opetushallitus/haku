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

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.haku.oppija.hakemus.ApplicationGenerator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Test;
import org.openqa.selenium.By;

import static fi.vm.sade.haku.oppija.ui.selenium.DefaultValues.KYSYMYS_POHJAKOULUTUS;
import static fi.vm.sade.haku.oppija.ui.selenium.DefaultValues.TUTKINTO_PERUSKOULU;
import static org.junit.Assert.assertFalse;

public class HAK305IT extends DummyModelBaseItTest {

    public static final String NATIVE_LANGUAGE_FI = "FI";
    public static final String NATIVE_LANGUAGE_SV = "SV";

    @Test
    public void submitApplication() throws Exception {
        navigateToFirstPhase();

        fillOutTheHenkilotiedotPhase(NATIVE_LANGUAGE_FI);

        nextPhase(OppijaConstants.PHASE_EDUCATION);

        fillOutTheKoulutustaustaPhase(NATIVE_LANGUAGE_FI);

        nextPhase(OppijaConstants.PHASE_APPLICATION_OPTIONS);

        fillInTheHakutoiveetPhase();

        nextPhase(OppijaConstants.PHASE_GRADES);

        fillInArvosanatTheme();

        // Native lang == FI, no lang test
        elementsNotPresentByName("yleinen_kielitutkinto_sv", "valtionhallinnon_kielitutkinto_sv",
                "yleinen_kielitutkinto_fi", "valtionhallinnon_kielitutkinto_sv");

        findByIdAndClick("nav-henkilotiedot");
        setNativeLanguage(NATIVE_LANGUAGE_SV);
        nextPhase(OppijaConstants.PHASE_EDUCATION); // Koulutustausta
        setPerusopetuksenKieli(NATIVE_LANGUAGE_SV);
        nextPhase(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        nextPhase(OppijaConstants.PHASE_GRADES); // Osaaminen
        select();
        selectByValue("PK_AI_OPPIAINE", "SV");
        selectByValue("PK_A2_OPPIAINE", "SE");
        clickByXPath("//td[@id='PK_B1_column1']//a");
        nextPhase(OppijaConstants.PHASE_MISC);
        fillInRestOfThePhasesAndCheckTheOID();
    }


    private void fillInArvosanatTheme() {
        findById("arvosanat");
        select();
        selectByValue("PK_AI_OPPIAINE", "FI");
        selectByValue("PK_A1_OPPIAINE", "EN");
        selectByValue("PK_B1_OPPIAINE", "SE");
        //driver.findElement(new By.ById("KielitaitokysymyksetTheme"));
    }

    private void fillInRestOfThePhasesAndCheckTheOID() {
        // Lisätiedot
        clickAllElementsByXPath("//input[@type='checkbox']");
        setValue("TYOKOKEMUSKUUKAUDET", "2");
        clickByNameAndValue("asiointikieli", "suomi");
        nextPhase(OppijaConstants.PHASE_PREVIEW);

        // Esikatselu
        nextPhase(OppijaConstants.PHASE_PREVIEW);
        findByIdAndClick("submit_confirm");

        String oid = seleniumContainer.getDriver().findElement(new By.ByClassName("number")).getText();
        assertFalse(oid.startsWith(ApplicationGenerator.oidPrefix));
    }


    private void fillOutTheHenkilotiedotPhase(final String aidinkieli) {
        fillOut(defaultValues.getHenkilotiedot(ImmutableMap.of("aidinkieli", aidinkieli)));
    }

    private void fillOutTheKoulutustaustaPhase(final String opetuskieli) {
        setValue(KYSYMYS_POHJAKOULUTUS, TUTKINTO_PERUSKOULU);
        setValue(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_KYMPPI, "true");
        setValue(OppijaConstants.KYMPPI_PAATTOTODISTUSVUOSI, "2012");
        setPerusopetuksenKieli(opetuskieli);
    }

    private void fillInTheHakutoiveetPhase() {
        findById("preference1-Opetuspiste");
        typeWithoutTab("preference1-Opetuspiste", "Esp");
        clickLinkByText("FAKTIA, Espoo op");
        clickByXPath("//option[@value='Kaivosalan perustutkinto, pk']");
        fillOut(defaultValues.preference1);
    }


    private void setPerusopetuksenKieli(final String opetuskieli) {
        setValue(OppijaConstants.PERUSOPETUS_KIELI, opetuskieli);
    }

    private void setNativeLanguage(final String aidinkieli) {
        setValue("aidinkieli", aidinkieli);
    }

}
