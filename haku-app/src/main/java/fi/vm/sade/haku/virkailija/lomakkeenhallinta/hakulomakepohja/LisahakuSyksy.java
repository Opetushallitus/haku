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
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.hakutoiveet.HakutoiveetPhaseLisahakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.henkilotiedot.HenkilotiedotPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta.KoulutustaustaPhaseSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot.LisatiedotPhaseLisahakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.OsaaminenPhaseSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.valmis.ValmisPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.List;

public class LisahakuSyksy {

    private static final String FORM_MESSAGES = "form_messages_lisahaku_syksy";
    private static final String FORM_ERRORS = "form_errors_lisahaku_syksy";
    private static final String FORM_VERBOSE_HELP = "form_verboseHelp_lisahaku_syksy";

    public static Form generateForm(final ApplicationSystem as, final KoodistoService koodistoService) {
        try {
            Form form = new Form(as.getId(), as.getName());
            form.addChild(HenkilotiedotPhase.create(OppijaConstants.LISA_HAKU, koodistoService, FORM_MESSAGES, FORM_ERRORS, FORM_VERBOSE_HELP));
            form.addChild(KoulutustaustaPhaseSyksy.create(koodistoService, FORM_MESSAGES, FORM_ERRORS, FORM_VERBOSE_HELP));
            form.addChild(HakutoiveetPhaseLisahakuSyksy.create());
            form.addChild(OsaaminenPhaseSyksy.create(koodistoService, FORM_MESSAGES, FORM_ERRORS, FORM_VERBOSE_HELP));
            form.addChild(LisatiedotPhaseLisahakuSyksy.create());
            return form;
        } catch (Exception e) {
            throw new RuntimeException(LisahakuSyksy.class.getCanonicalName() + " init failed", e);
        }
    }

    public static List<Element> generateApplicationCompleteElements() {
        return ValmisPhase.create(FORM_MESSAGES, "form.valmis.muutoksentekeminen.p1");

    }

    public static List<Element> createAdditionalInformationElements() {
        return ValmisPhase.createAdditionalInformationElements(FORM_MESSAGES);
    }
}
