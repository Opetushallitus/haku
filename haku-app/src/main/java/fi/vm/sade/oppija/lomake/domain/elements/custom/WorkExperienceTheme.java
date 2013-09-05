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
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomakkeenhallinta.util.OppijaConstants;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.Transient;

import java.util.Date;

/**
 * @author Hannu Lyytikainen
 */
public class WorkExperienceTheme extends Theme {

    private static final long serialVersionUID = -750846124976213900L;
    // degree type that needs to be applied to
    // so that this phase is rendered
    private String requiredEducationDegree;

    // Applicant must be born before this day to get asked about work experience.
    private final Date referenceDate;

    public WorkExperienceTheme(@JsonProperty(value = "id") String id,
                               @JsonProperty(value = "i18nText") I18nText i18nText,
                               @JsonProperty(value = "requiredEducationDegree") String requiredEducationDegree,
                               @JsonProperty(value = "referenceDate") Date referenceDate) {
        super(id, i18nText, true);
        this.requiredEducationDegree = requiredEducationDegree;
        this.referenceDate = referenceDate;
    }

    @Transient
    public String[] getAoEducationDegreeKeys() {
        return OppijaConstants.AO_EDUCATION_DEGREE_KEYS;
    }

    public void setRequiredEducationDegree(String requiredEducationDegree) {
        this.requiredEducationDegree = requiredEducationDegree;
    }

    public String getRequiredEducationDegree() {
        return requiredEducationDegree;
    }

    public Date getReferenceDate() {
        return referenceDate;
    }
}
