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
import fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.phase.hakutoiveet.HakutoiveetPhaseLisahakuSyksy;
import fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.phase.henkilotiedot.HenkilotiedotPhaseLisahakuSyksy;
import fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta.KoulutustaustaPhaseLisahakuSyksy;
import fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot.LisatiedotPhaseLisahakuSyksy;
import fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.OsaaminenPhaseLisahakuSyksy;
import fi.vm.sade.oppija.lomakkeenhallinta.koodisto.KoodistoService;

import java.util.Date;

public class LisahakuSyksy {

    public static Form generateForm(final ApplicationSystem as, final KoodistoService koodistoService) {
        try {
            Form form = new Form(as.getId(), as.getName());
            form.addChild(HenkilotiedotPhaseLisahakuSyksy.create(koodistoService));
            form.addChild(KoulutustaustaPhaseLisahakuSyksy.create(koodistoService));
            form.addChild(HakutoiveetPhaseLisahakuSyksy.create());
            form.addChild(OsaaminenPhaseLisahakuSyksy.create(koodistoService));
            Date start = as.getApplicationPeriods() != null && !as.getApplicationPeriods().isEmpty() ?
                    as.getApplicationPeriods().get(0).getStart() : new Date();
            form.addChild(LisatiedotPhaseLisahakuSyksy.create(start));
            return form;
        } catch (Exception e) {
            throw new RuntimeException(LisahakuSyksy.class.getCanonicalName() + " init failed", e);
        }
    }
}
