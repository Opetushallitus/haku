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

package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013;

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

import java.util.Date;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NForm;

@Service
public class Yhteishaku2013 {

    public static final String FORM_ID = "yhteishaku";
    private final ApplicationPeriod applicationPeriod;

    @Autowired
    public Yhteishaku2013(
            final KoodistoService koodistoService,
            @Value("${asid}") String asid,
            @Value("${aoid}") String aoid) { // NOSONAR
        this.applicationPeriod = new ApplicationPeriod(asid);
        Form form = createForm(koodistoService, aoid, applicationPeriod.getStarts());
        applicationPeriod.addForm(form);
    }


    private Form createForm(final KoodistoService koodistoService, final String aoidAdditionalQuestion, final Date start) {
        try {
            Form form = new Form(FORM_ID, createI18NForm("form.title"));
            form.addChild(HenkilotiedotPhase.create(koodistoService));
            form.addChild(KoulutustaustaPhase.create(koodistoService));
            form.addChild(HakutoiveetPhase.create(aoidAdditionalQuestion));
            form.addChild(OsaaminenPhase.create(koodistoService));
            form.addChild(LisatiedotPhase.create(start));
            form.addChild(EsikatseluPhase.create(form));
            return form;
        } catch (Exception e) {
            throw new RuntimeException(Yhteishaku2013.class.getCanonicalName() + " init failed", e);
        }
    }

    public ApplicationPeriod getApplicationPeriod() {
        return applicationPeriod;
    }
}
