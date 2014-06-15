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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.PhaseBuilder.Phase;

public class OsaaminenPhase {

    public static Element create(final FormParameters formParameters) {
        Element osaaminen = Phase("osaaminen").formParams(formParameters).build();
        if (formParameters.getFormTemplateType().equals(FormParameters.FormTemplateType.YHTEISHAKU_KEVAT) ||
                formParameters.isPervako() || formParameters.isKevaanLisahaku()) {
            osaaminen.addChild(ArvosanatTheme.createArvosanatThemeKevat(formParameters));
        } else {
            osaaminen.addChild(ArvosanatTheme.createArvosanatTheme(formParameters));
        }
        if (!formParameters.isPervako()) {
            osaaminen.addChild(KielitaitokysymyksetTheme.createKielitaitokysymyksetTheme(formParameters));
        }
        return osaaminen;

    }
}
