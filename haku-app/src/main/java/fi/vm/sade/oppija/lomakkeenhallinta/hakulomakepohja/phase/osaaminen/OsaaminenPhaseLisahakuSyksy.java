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

package fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomakkeenhallinta.koodisto.KoodistoService;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NText;

public class OsaaminenPhaseLisahakuSyksy {

    private static final String FORM_MESSAGES = "form_messages_lisahaku_syksy";
    private static final String FORM_ERRORS = "form_errors_lisahaku_syksy";
    private static final String FORM_VERBOSE_HELP = "form_verboseHelp_lisahaku_syksy";

    public static Phase create(final KoodistoService koodistoService) {
        Phase osaaminen = new Phase("osaaminen", createI18NText("form.osaaminen.otsikko", FORM_MESSAGES), false);
        osaaminen.addChild(ArvosanatTheme.createArvosanatTheme(koodistoService, FORM_MESSAGES, FORM_ERRORS, FORM_VERBOSE_HELP));
        osaaminen.addChild(KielitaitokysymyksetTheme.createKielitaitokysymyksetTheme(FORM_MESSAGES, FORM_ERRORS, FORM_VERBOSE_HELP));
        return osaaminen;
    }
}
