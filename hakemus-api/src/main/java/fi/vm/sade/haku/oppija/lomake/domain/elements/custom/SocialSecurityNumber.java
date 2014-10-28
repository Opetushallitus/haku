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
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import java.util.HashMap;
import java.util.Map;

public class SocialSecurityNumber extends Question {

    private static final long serialVersionUID = -5573908500482185095L;

    public static final String HENKILOTUNNUS_HASH = "Henkilotunnus_digest";
    public static final String HENKILOTUNNUS = "Henkilotunnus";

    private Option maleOption;
    private Option femaleOption;
    private String sexId;
    private I18nText sexI18nText;

    public SocialSecurityNumber(final String id, final I18nText i18nText, final I18nText sexI18nText,
                                final Option maleOption, final Option femaleOption, final String sexId) {
        super(id, i18nText);
        this.maleOption = maleOption;
        this.femaleOption = femaleOption;
        this.sexId = sexId;
        this.sexI18nText = sexI18nText;
    }

    public Option getMaleOption() {
        return maleOption;
    }

    public Option getFemaleOption() {
        return femaleOption;
    }

    public String getSexId() {
        return sexId;
    }

    public I18nText getSexI18nText() {
        return sexI18nText;
    }

    @Override
    public Element[] getExtraExcelColumns() {
        Element[] extras = new Element[2];
        extras[0] = new SsnDateOfBirth("ssnDateOfBirthh", ElementUtil.createI18NText("syntymaaika"));
        extras[1] = new SsnSex("ssnSex", ElementUtil.createI18NText("sukupuoli"));
        return extras;
    }

    class SsnDateOfBirth extends Question {
        private final Map<String, String> centuries = new HashMap<String, String>(4) {{
            put("+", "18"); put("-", "19"); put("a", "20"); put("A", "20");
        }};

        public SsnDateOfBirth(final String id, final I18nText i18nText) {
            super(id, i18nText);
        }

        @Override
        public String getExcelValue(String value, String lang) {
            String day = value.substring(0, 2);
            String month = value.substring(2, 4);
            String year = value.substring(4, 6);
            String century = value.substring(6, 7);
            return new StringBuilder()
                    .append(day).append(".")
                    .append(month).append(".")
                    .append(centuries.get(century)).append(year)
                    .toString();
        }
    }

    class SsnSex extends Question {
        private final I18nText SUKUPUOLI_MIES = ElementUtil.createI18NText("sukupuoli.mies");
        private final I18nText SUKUPUOLI_NAINEN = ElementUtil.createI18NText("sukupuoli.nainen");

        public SsnSex(final String id, final I18nText i18nText) {
            super(id, i18nText);
        }


        @Override
        public String getExcelValue(String value, String lang) {
            int sexNumber = Integer.valueOf(value.substring(9, 10));
            return sexNumber % 2 == 0
                    ? SUKUPUOLI_NAINEN.getTranslations().get(lang)
                    : SUKUPUOLI_MIES.getTranslations().get(lang);
        }
    }
}
