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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl;

import com.google.common.base.Function;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.OptionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.koodisto.service.types.common.KoodiType;

public class KoodiTypeToOptionFunction implements Function<KoodiType, Option> {

    @Override
    public Option apply(final KoodiType koodiType) {
        return (Option) OptionBuilder.Option(koodiType.getKoodiArvo())
                .setValue(koodiType.getKoodiArvo())
                .i18nText(TranslationsUtil.createTranslationsMap(koodiType))
                .build();
    }

}
