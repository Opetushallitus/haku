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

package fi.vm.sade.haku.oppija.lomake.domain.elements.custom;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.haku.oppija.lomake.validation.validators.RequiredFieldValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Mikko Majapuro
 */
public class SinglePreference extends Titled {

    private I18nText educationLabel;
    private I18nText learningInstitutionLabel;
    private I18nText childLONameListLabel;

    public SinglePreference(@JsonProperty(value = "id") final String id,
                            @JsonProperty(value = "educationLabel") final I18nText educationLabel,
                            @JsonProperty(value = "learningInstitutionLabel") final I18nText learningInstitutionLabel,
                            @JsonProperty(value = "i18nText") final I18nText i18nText,
                            @JsonProperty(value = "childLONameListLabel") final I18nText childLONameListLabel
    ) {
        super(id, i18nText);
        this.educationLabel = educationLabel;
        this.learningInstitutionLabel = learningInstitutionLabel;
        this.childLONameListLabel = childLONameListLabel;
        setValidator(new RequiredFieldValidator(getId() + "-Opetuspiste", ElementUtil.createI18NText("yleinen.pakollinen",
                "form_errors_yhteishaku_kevat")));
        setValidator(new RequiredFieldValidator(getId() + "-Koulutus", ElementUtil.createI18NText("yleinen.pakollinen",
                "form_errors_yhteishaku_kevat")));
    }

    public I18nText getEducationLabel() {
        return educationLabel;
    }

    public I18nText getLearningInstitutionLabel() {
        return learningInstitutionLabel;
    }

    public I18nText getChildLONameListLabel() {
        return childLONameListLabel;
    }
}
