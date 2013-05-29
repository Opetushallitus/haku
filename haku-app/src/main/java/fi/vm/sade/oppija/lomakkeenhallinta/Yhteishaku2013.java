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

package fi.vm.sade.oppija.lomakkeenhallinta;

import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.esikatselu.EsikatseluPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.hakutoiveet.HakutoiveetPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.henkilotiedot.HenkilotiedotPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.koulutustausta.KoulutustaustaPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.lisatiedot.LisatiedotPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.OsaaminenPhase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NForm;

@Service
public class Yhteishaku2013 {

    public static final String TUTKINTO_ULKOMAILLA_NOTIFICATION_ID = "tutkinto7-notification";
    public static final String TUTKINTO_KESKEYTNYT_NOTIFICATION_ID = "tutkinto5-notification";
    public static final String FORM_ID = "yhteishaku";

    public static final String HAKUTOIVEET_PHASE_ID = "hakutoiveet";
    public static final String TUTKINTO_KESKEYTYNYT = "tutkinto7";
    public static final String TUTKINTO_YLIOPPILAS = "tutkinto9";
    public static final String TUTKINTO_ULKOMAINEN_TUTKINTO = "tutkinto0";
    public static final String TUTKINTO_PERUSKOULU = "tutkinto1";

    private final ApplicationPeriod applicationPeriod;


    public String aoidAdditionalQuestion = "1.2.246.562.14.71344129359";
    public static final String MOBILE_PHONE_PATTERN =
            "^$|^(?!\\+358|0)[\\+]?[0-9\\-\\s]+$|^(\\+358|0)[\\-\\s]*((4[\\-\\s]*[0-6])|50)[0-9\\-\\s]*$";

    private final KoodistoService koodistoService;

    @Autowired // NOSONAR
    public Yhteishaku2013(
            final KoodistoService koodistoService,
            @Value("${asid}") String asid,
            @Value("${aoid}") String aoid) { // NOSONAR
        this.koodistoService = koodistoService;
        this.applicationPeriod = new ApplicationPeriod(asid);
        this.aoidAdditionalQuestion = aoid;
        createFrom();
    }


    public void createFrom() { // NOSONAR
        try {

            Form form = new Form(FORM_ID, createI18NForm("form.title"));

            applicationPeriod.addForm(form);

            HenkilotiedotPhase henkilotiedotPhase = new HenkilotiedotPhase(koodistoService);
            form.addChild(henkilotiedotPhase.getHenkilotiedot());

            KoulutustaustaPhase koulutustaustaPhase = new KoulutustaustaPhase(koodistoService);
            form.addChild(koulutustaustaPhase.getKoulutustausta());

            HakutoiveetPhase hakutoiveetPhase = new HakutoiveetPhase(aoidAdditionalQuestion);
            form.addChild(hakutoiveetPhase.getHakutoiveet());

            OsaaminenPhase osaaminenPhase = new OsaaminenPhase(koodistoService);
            form.addChild(osaaminenPhase.getOsaaminen());

            LisatiedotPhase lisatiedotPhase = new LisatiedotPhase(applicationPeriod.getStarts());
            form.addChild(lisatiedotPhase.getLisatiedot());

            EsikatseluPhase esikatseluPhase = new EsikatseluPhase(form);
            form.addChild(esikatseluPhase.getEsikatselu());

        } catch (Exception e) {
            throw new RuntimeException(Yhteishaku2013.class.getCanonicalName() + " init failed", e);
        }

    }

    public ApplicationPeriod getApplicationPeriod() {
        return applicationPeriod;
    }
}
