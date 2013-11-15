/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja;

import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.phase.hakutoiveet.HakutoiveetPhaseYhteishakuKevat;
import fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.phase.henkilotiedot.HenkilotiedotPhaseYhteishakuKevat;
import fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta.KoulutustaustaPhaseYhteishakuKevat;
import fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot.LisatiedotPhaseYhteishakuKevat;
import fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.OsaaminenPhaseYhteishakuKevat;
import fi.vm.sade.oppija.lomakkeenhallinta.koodisto.KoodistoService;

import java.util.Date;

public class YhteishakuKevat {

    public static Form generateForm(final ApplicationSystem as, final KoodistoService koodistoService) {
        try {
            Form form = new Form(as.getId(), as.getName());
            form.addChild(HenkilotiedotPhaseYhteishakuKevat.create(koodistoService));
            form.addChild(KoulutustaustaPhaseYhteishakuKevat.create(koodistoService));
            form.addChild(HakutoiveetPhaseYhteishakuKevat.create());
            form.addChild(OsaaminenPhaseYhteishakuKevat.create(koodistoService));
            Date start = as.getApplicationPeriods() != null && !as.getApplicationPeriods().isEmpty() ?
                    as.getApplicationPeriods().get(0).getStart() : new Date();
            form.addChild(LisatiedotPhaseYhteishakuKevat.create(start));
            return form;
        } catch (Exception e) {
            throw new RuntimeException(YhteishakuKevat.class.getCanonicalName() + " init failed", e);
        }
    }
}
