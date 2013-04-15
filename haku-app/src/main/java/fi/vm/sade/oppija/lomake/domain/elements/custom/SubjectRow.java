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

package fi.vm.sade.oppija.lomake.domain.elements.custom;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class SubjectRow extends Question {

    private static final long serialVersionUID = -7775452756396204586L;

    private final boolean optional;
    private final boolean highSchool;
    private final boolean comprehensiveSchool;
    private final boolean language;

    public SubjectRow(@JsonProperty(value = "id") final String id,
                      @JsonProperty(value = "i18nText") final I18nText i18nText,
                      @JsonProperty(value = "optional") boolean optional,
                      @JsonProperty(value = "highSchool") boolean highSchool,
                      @JsonProperty(value = "comprehensiveSchool") boolean comprehensiveSchool,
                      @JsonProperty(value = "language") boolean language) {
        super(id, i18nText);
        this.optional = optional;
        this.highSchool = highSchool;
        this.comprehensiveSchool = comprehensiveSchool;
        this.language = language;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isHighSchool() {
        return highSchool;
    }

    public boolean isComprehensiveSchool() {
        return comprehensiveSchool;
    }

    public boolean isLanguage() {
        return language;
    }
    @JsonIgnore
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SubjectRow");
        sb.append("{optional=").append(optional);
        sb.append(", highSchool=").append(highSchool);
        sb.append(", comprehensiveSchool=").append(comprehensiveSchool);
        sb.append(", language=").append(language);
        sb.append("{i18nText=").append(getI18nText());
        sb.append('}');
        return sb.toString();
    }
}
