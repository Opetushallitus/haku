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
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Question;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.Transient;

/**
 * Renders as a user's application preference row. Title is used to hold the name of the preference row (Hakutoive 1, Hakutoive 2 etc.)
 * Options are used to hold the different educations.
 *
 * @author Mikko Majapuro
 */
public class PreferenceRow extends Question {

    private static final long serialVersionUID = 5149303147942411002L;
    // label text for reset button
    private I18nText resetLabel;
    // label text for education dropDatabase down select
    private I18nText educationLabel;
    // label text for learning institution input (Opetuspiste)
    private I18nText learningInstitutionLabel;
    // label text for the child learning opportunity list
    private I18nText childLONameListLabel;
    // label text for attachments
    private I18nText attachmentsLabel;
    // label text for payment
    private I18nText paymentLabel;

    public PreferenceRow(@JsonProperty(value = "id") final String id,
                         @JsonProperty(value = "resetLabel") final I18nText resetLabel,
                         @JsonProperty(value = "educationLabel") final I18nText educationLabel,
                         @JsonProperty(value = "learningInstitutionLabel") final I18nText learningInstitutionLabel,
                         @JsonProperty(value = "childLONameListLabel") final I18nText childLONameListLabel,
                         @JsonProperty(value = "attachmentsLabel") final I18nText attachmentsLabel,
                         @JsonProperty(value = "paymentLabel") final I18nText paymentLabel) {
        super(id, null);
        this.resetLabel = resetLabel;
        this.educationLabel = educationLabel;
        this.learningInstitutionLabel = learningInstitutionLabel;
        this.childLONameListLabel = childLONameListLabel;
        this.attachmentsLabel = attachmentsLabel;
        this.paymentLabel = paymentLabel;
    }

    public I18nText getResetLabel() {
        return resetLabel;
    }

    public I18nText getEducationLabel() {
        return educationLabel;
    }

    public I18nText getLearningInstitutionLabel() {
        return learningInstitutionLabel;
    }

    public I18nText getAttachmentsLabel() {
        return attachmentsLabel;
    }

    public I18nText getPaymentLabel() {
        return paymentLabel;
    }

    public I18nText getChildLONameListLabel() {
        return childLONameListLabel;
    }

    @Transient
    public String getLearningInstitutionInputId() {
        return this.id + "-Opetuspiste";
    }

    @Transient
    public String getEducationInputId() {
        return this.id + "-Koulutus";
    }

    @Transient
    public String getEducationDegreeId() {
        return this.id + "-Koulutus-educationDegree";
    }

    @Transient
    public String getEducationOidInputId() {
        return this.id + "-Koulutus-id";
    }

    @Transient
    public String getLearningInstitutionOidInputId() {
        return this.id + "-Opetuspiste-id";
    }

}
