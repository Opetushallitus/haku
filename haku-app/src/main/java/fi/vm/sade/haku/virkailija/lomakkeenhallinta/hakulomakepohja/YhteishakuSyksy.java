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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.henkilotiedot.HenkilotiedotPhaseYhteishakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta.KoulutustaustaPhaseYhteishakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot.LisatiedotPhaseYhteishakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.OsaaminenPhaseYhteishakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.valmis.ValmisPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class YhteishakuSyksy {

    private static final String FORM_MESSAGES = "form_messages_yhteishaku_syksy";

    public static Form generateForm(final ApplicationSystem as, final KoodistoService koodistoService) {
        try {
            Form form = new Form(as.getId(), as.getName());
            form.addChild(HenkilotiedotPhaseYhteishakuSyksy.create(koodistoService));
            form.addChild(KoulutustaustaPhaseYhteishakuSyksy.create(koodistoService));
            form.addChild(HakutoiveetPhaseYhteishakuSyksy.create());
            form.addChild(OsaaminenPhaseYhteishakuSyksy.create(koodistoService));
            form.addChild(LisatiedotPhaseYhteishakuSyksy.create());
            return form;
        } catch (Exception e) {
            throw new RuntimeException(YhteishakuSyksy.class.getCanonicalName() + " init failed", e);
        }
    }

    public static List<Element> createApplicationCompleteElements() {
        return ValmisPhase.create(FORM_MESSAGES, "form.valmis.muutoksentekeminen.p1",
                "form.valmis.muutoksentekeminen.p3", "form.valmis.muutoksentekeminen.p3");
    }
}
