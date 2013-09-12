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
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.esikatselu.EsikatseluPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.hakutoiveet.HakutoiveetPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.henkilotiedot.HenkilotiedotPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.koulutustausta.KoulutustaustaPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.lisatiedot.LisatiedotPhase;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.OsaaminenPhase;

import java.util.Date;

public class Yhteishaku2013 {

    public static Form generateForm(final ApplicationSystem as, final KoodistoService koodistoService) {
        try {
            Form form = new Form(as.getId(), as.getName());
            form.addChild(HenkilotiedotPhase.create(koodistoService));
            form.addChild(KoulutustaustaPhase.create(koodistoService));
            form.addChild(HakutoiveetPhase.create());
            form.addChild(OsaaminenPhase.create(koodistoService));
            Date start = as.getApplicationPeriods() != null && !as.getApplicationPeriods().isEmpty() ? as.getApplicationPeriods().get(0).getStart() :
                    new Date();
            form.addChild(LisatiedotPhase.create(start));
            //form.addChild(EsikatseluPhase.create(form));
            return form;
        } catch (Exception e) {
            throw new RuntimeException(Yhteishaku2013.class.getCanonicalName() + " init failed", e);
        }
    }
}
