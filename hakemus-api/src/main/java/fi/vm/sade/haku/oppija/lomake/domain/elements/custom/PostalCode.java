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

package fi.vm.sade.haku.oppija.lomake.domain.elements.custom;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.OptionQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import java.util.List;

public class PostalCode extends OptionQuestion {

    public PostalCode(final String id, final I18nText i18nText, final List<Option> options) {
        super(id, i18nText, options);
    }

    @Override
    public Element[] getExtraExcelColumns() {
        Element[] elements = new Element[1];
        elements[0] = new PostalOffice(this);
        return elements;
    }

    @Override
    public String getExcelValue(String answer, String lang) {
        return answer;
    }

    class PostalOffice extends OptionQuestion {

        public PostalOffice(PostalCode postalCode) {
            super("Postitoimipaikka", ElementUtil.createI18NText("postitoimipaikka") , postalCode.getOptions());
        }
    }
}
