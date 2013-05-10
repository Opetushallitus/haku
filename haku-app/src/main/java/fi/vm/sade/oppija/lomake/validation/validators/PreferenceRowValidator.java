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

package fi.vm.sade.oppija.lomake.validation.validators;

import java.util.HashMap;
import java.util.Map;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SoraQuestion;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomake.validation.Validator;

/**
 * @author Hannu Lyytikainen
 */
public class PreferenceRowValidator implements Validator {

    private String educationDegreeInputId;
    private String discretionaryEducationDegree;
    private String discretionaryInputId;
    private SoraQuestion soraQuestion;

    public PreferenceRowValidator(String educationDegreeInputId, String discretionaryEducationDegree,
                                  String discretionaryInputId, SoraQuestion soraQuestion) {
        this.educationDegreeInputId = educationDegreeInputId;
        this.discretionaryEducationDegree = discretionaryEducationDegree;
        this.discretionaryInputId = discretionaryInputId;
        this.soraQuestion = soraQuestion;
    }

    @Override
    public ValidationResult validate(Map<String, String> values) {
        final Map<String, I18nText> errors = new HashMap<String, I18nText>();
        if (values.get(educationDegreeInputId) != null
                && values.get(educationDegreeInputId).equals(discretionaryEducationDegree)
                && values.get(discretionaryInputId) == null) {
            errors.put(discretionaryInputId, ElementUtil.createI18NTextError("yleinen.pakollinen"));
        }
        
        if (soraQuestion.isSoraRequired()) {
            for (Radio q : this.soraQuestion.getQuestions()) {
                if (values.get(q.getId()) == null) {
                    errors.put(q.getId(), ElementUtil.createI18NTextError("yleinen.pakollinen"));
                }
            }
        }
        
        return new ValidationResult(errors);
    }

}
