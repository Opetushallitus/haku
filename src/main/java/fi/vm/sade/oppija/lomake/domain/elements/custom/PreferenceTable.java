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
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.oppija.lomake.validation.Validator;
import fi.vm.sade.oppija.lomake.validation.validators.PreferenceTableValidator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Preference table element with row sorting functionality
 *
 * @author Mikko Majapuro
 */
public class PreferenceTable extends Titled {

    // label text for up button
    private String moveUpLabel;
    // label text for down button
    private String moveDownLabel;

    public PreferenceTable(@JsonProperty(value = "id") final String id,
                           @JsonProperty(value = "i18nText") final I18nText i18nText,
                           @JsonProperty(value = "moveUpLabel") final String moveUpLabel,
                           @JsonProperty(value = "moveDownLabel") final String moveDownLabel) {
        super(id, i18nText);
        this.moveUpLabel = moveUpLabel;
        this.moveDownLabel = moveDownLabel;
    }

    public String getMoveUpLabel() {
        return moveUpLabel;
    }

    public String getMoveDownLabel() {
        return moveDownLabel;
    }

    @Override
    @JsonIgnore
    public List<Validator> getValidators() {
        List<Validator> listOfValidators = new ArrayList<Validator>();
        List<String> learningInstitutionInputIds = new ArrayList<String>();
        List<String> educationInputIds = new ArrayList<String>();

        for (Element element : this.getChildren()) {
            PreferenceRow pr = (PreferenceRow) element;
            learningInstitutionInputIds.add(pr.getLearningInstitutionInputId());
            educationInputIds.add(pr.getEducationInputId());
        }

        listOfValidators.add(new PreferenceTableValidator(learningInstitutionInputIds, educationInputIds));
        return listOfValidators;
    }
}
