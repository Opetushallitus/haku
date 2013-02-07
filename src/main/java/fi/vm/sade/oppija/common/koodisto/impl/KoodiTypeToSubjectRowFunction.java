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

package fi.vm.sade.oppija.common.koodisto.impl;

import com.google.common.base.Function;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;

import java.util.Map;

public class KoodiTypeToSubjectRowFunction implements Function<KoodiType, SubjectRow> {

    @Override
    public SubjectRow apply(final KoodiType koodiType) {
        Map<String, String> translationsMap = TranslationsUtil.createTranslationsMap(koodiType);
        String koodiArvo = koodiType.getKoodiArvo();
        return new SubjectRow(koodiArvo, new I18nText(koodiArvo, translationsMap));
    }
}
