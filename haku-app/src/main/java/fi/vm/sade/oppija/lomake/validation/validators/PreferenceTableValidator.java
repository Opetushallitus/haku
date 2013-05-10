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

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomake.validation.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Preference table validator
 *
 * @author Mikko Majapuro
 */
public class PreferenceTableValidator implements Validator {

    private final int rowCount;
    private final List<String> learningInstitutionInputIds;
    private final List<String> educationInputIds;
    private List<String> learningInstitutions;
    private List<String> educationDegreeInputIds;
    private List<String> educations;

    public PreferenceTableValidator(final List<String> learningInstitutionInputIds, final List<String> educationInputIds,
                                    final List<String> educationDegreeInputIds) {
        this.educationDegreeInputIds = educationDegreeInputIds;
        assert learningInstitutionInputIds.size() == educationInputIds.size();
        this.learningInstitutionInputIds = learningInstitutionInputIds;
        this.educationInputIds = educationInputIds;
        this.rowCount = learningInstitutionInputIds.size();
    }

    @Override
    public ValidationResult validate(Map<String, String> values) {
        learningInstitutions = new ArrayList<String>();
        educations = new ArrayList<String>();
        final Map<String, I18nText> errors = new HashMap<String, I18nText>();

        for (int i = 0; i < rowCount; ++i) {
            String learningInstitutionInputId = learningInstitutionInputIds.get(i);
            String educationInputId = educationInputIds.get(i);
            String learningInstitution = values.get(learningInstitutionInputId);
            String education = values.get(educationInputId);

            if (!checkBothNullOrTyped(learningInstitution, education)) {
                errors.put(education == null || education.isEmpty() ? educationInputId : learningInstitutionInputId,
                        ElementUtil.createI18NTextError("yleinen.pakollinen"));
            }

            if (!checkUnique(learningInstitution, education)) {
                errors.put(educationInputId, ElementUtil.createI18NTextError("hakutoiveet.duplikaatteja"));
            }

            if (!checkEmptyRowBeforeGivenPreference(educations, education)) {
                errors.put(educationInputId, ElementUtil.createI18NTextError("hakutoiveet.tyhjia"));
            }

            learningInstitutions.add(learningInstitution);
            educations.add(education);

        }
        return new ValidationResult(errors);
    }

    /**
     * Checks that the both preference input fields has values or the both is null
     *
     * @param learningInstitution learning institution input value
     * @param education           education input value
     * @return true if valid, false otherwise
     */
    private boolean checkBothNullOrTyped(final String learningInstitution, final String education) {
        return !((learningInstitution == null || learningInstitution.isEmpty()) ^ (education == null || education.isEmpty()));
    }

    /**
     * Checks that the given preference is unique in a preference table
     *
     * @param learningInstitution learning institute input value
     * @param education           education input value
     * @return true if valid, false otherwise
     */
    private boolean checkUnique(final String learningInstitution, final String education) {
        assert learningInstitutions.size() == educations.size();

        if (learningInstitution != null && !learningInstitution.isEmpty() && education != null &&
                !education.isEmpty()) {
            for (int i = 0; i < learningInstitutions.size(); ++i) {
                if (learningInstitution.equals(learningInstitutions.get(i)) &&
                        education.equals(educations.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks that there are no empty preference rows before the given preference
     *
     * @param values
     * @param value
     * @return true if valid, false otherwise
     */
    private boolean checkEmptyRowBeforeGivenPreference(final List<String> values, final String value) {
        return !(value != null && !value.isEmpty() && !values.isEmpty() && (values.get(values.size() - 1) == null || values.get(values.size() - 1).isEmpty()));
    }
}
