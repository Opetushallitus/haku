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

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot.Lisatiedot.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NText;

public final class LisatiedotPhaseLisahakuSyksy {

    private static final String FORM_MESSAGES = "form_messages_lisahaku_syksy";
    private static final String FORM_ERRORS = "form_errors_lisahaku_syksy";
    private static final String FORM_VERBOSE_HELP = "form_verboseHelp_lisahaku_syksy";

    private LisatiedotPhaseLisahakuSyksy() {
    }

    public static Phase create() {
        Phase lisatiedot = new Phase("lisatiedot", createI18NText("form.lisatiedot.otsikko", FORM_MESSAGES), false,
                Lists.newArrayList("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO"));
        lisatiedot.addChild(createTyokokemus(FORM_MESSAGES,FORM_ERRORS,FORM_VERBOSE_HELP));
        lisatiedot.addChild(createLupatiedot(FORM_MESSAGES,FORM_ERRORS,FORM_VERBOSE_HELP));
        lisatiedot.addChild(createUrheilijanLisakysymykset(FORM_MESSAGES,FORM_ERRORS,FORM_VERBOSE_HELP));
        return lisatiedot;
    }
}
