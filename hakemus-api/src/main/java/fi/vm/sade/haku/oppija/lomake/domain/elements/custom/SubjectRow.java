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
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;

public class SubjectRow extends Titled {

    private static final long serialVersionUID = -7775452756396204586L;

    private final boolean optional;
    private final boolean highSchool;
    private final boolean comprehensiveSchool;
    private final boolean language;

    public SubjectRow(final String id, final I18nText i18nText,
                      boolean optional, boolean highSchool, boolean comprehensiveSchool, boolean language) {
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

    public String toString() {
        return "(Id: " + this.id + ", comprehensiveSchool: " + this.comprehensiveSchool + ", highSchool: "
                + this.highSchool + ", optional: " + this.optional + ", language: " + this.language + ", translations: +"+ this.getI18nText().getText("fi") + ")";
    }
}
