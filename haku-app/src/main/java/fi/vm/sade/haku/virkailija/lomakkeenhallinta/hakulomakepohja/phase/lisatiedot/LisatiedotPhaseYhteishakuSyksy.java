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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.MessageBundleNames;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot.Lisatiedot.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NText;

public final class LisatiedotPhaseYhteishakuSyksy {


    private static final String FORM_MESSAGES = "form_messages_yhteishaku_syksy";
    private static final String FORM_ERRORS = "form_errors_yhteishaku_syksy";
    private static final String FORM_VERBOSE_HELP = "form_verboseHelp_yhteishaku_syksy";
    private static final MessageBundleNames MESSAGE_BUNDLE_NAMES = new MessageBundleNames(FORM_MESSAGES, FORM_ERRORS, FORM_VERBOSE_HELP);

    private LisatiedotPhaseYhteishakuSyksy() {
    }

    public static Phase create() {
        Phase lisatiedot = new Phase("lisatiedot", createI18NText("form.lisatiedot.otsikko", FORM_MESSAGES), false);
        lisatiedot.addChild(createTyokokemus(MESSAGE_BUNDLE_NAMES));
        lisatiedot.addChild(createLupatiedot(MESSAGE_BUNDLE_NAMES));
        lisatiedot.addChild(createUrheilijanLisakysymykset(MESSAGE_BUNDLE_NAMES));
        return lisatiedot;
    }
}
