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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.hakutoiveet.HakutoiveetPhaseYhteishakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.henkilotiedot.HenkilotiedotPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta.KoulutustaustaPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot.LisatiedotPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.OsaaminenPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.valmis.ValmisPhase;

import java.util.List;

public class YhteishakuSyksy {

    public static Form generateForm(final FormParameters formParameters) {
        try {
            ApplicationSystem as = formParameters.getApplicationSystem();
            Form form = new Form(as.getId(), as.getName());
            form.addChild(HenkilotiedotPhase.create(formParameters));
            form.addChild(KoulutustaustaPhase.create(formParameters));
            form.addChild(HakutoiveetPhaseYhteishakuSyksy.create(formParameters));
            form.addChild(OsaaminenPhase.create(formParameters));
            form.addChild(LisatiedotPhase.create(formParameters));
            return form;
        } catch (Exception e) {
            throw new RuntimeException(YhteishakuSyksy.class.getCanonicalName() + " init failed", e);
        }
    }

    public static List<Element> createApplicationCompleteElements(final ApplicationSystem as) {
        return ValmisPhase.create(as);
    }
}
