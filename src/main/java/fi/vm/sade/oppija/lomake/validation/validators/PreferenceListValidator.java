/*
 *
 *  * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *  *
 *  * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 *  * soon as they will be approved by the European Commission - subsequent versions
 *  * of the EUPL (the "Licence");
 *  *
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * European Union Public Licence for more details.
 *
 */

package fi.vm.sade.oppija.lomake.validation.validators;

import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomake.validation.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */
public class PreferenceListValidator implements Validator {

    private final int rowCount;
    private final List<String> learningInstitutionInputIds;
    private final List<String> educationInputIds;
    private List<String> learningInstitutions;
    private List<String> educations;
    private Map<String, String> errors;

    public PreferenceListValidator(final List<String> learningInstitutionInputIds, final List<String> educationInputIds) {
        assert learningInstitutionInputIds.size() == educationInputIds.size();
        this.learningInstitutionInputIds = learningInstitutionInputIds;
        this.educationInputIds = educationInputIds;
        this.rowCount = learningInstitutionInputIds.size();
    }

    @Override
    public ValidationResult validate(Map<String, String> values) {
        learningInstitutions = new ArrayList<String>();
        educations = new ArrayList<String>();
        errors = new HashMap<String, String>();

        for (int i  = 0; i < rowCount; ++i) {
            String learningInstitutionInputId = learningInstitutionInputIds.get(i);
            String educationInputId = educationInputIds.get(i);
            String learningInstitution = values.get(learningInstitutionInputId);
            String education = values.get(educationInputId);

            if (!checkBothNullOrTyped(learningInstitution, education)) {
                errors.put(education == null || education.isEmpty() ? educationInputId : learningInstitutionInputId, "Pakollinen tieto.");
            }

            if (!checkUnique(learningInstitution, education)) {
                errors.put(educationInputId, "Et voi syöttää samaa hakutoivetta useaan kertaan.");
            }

            if (!checkEmptyRowBeforeGivenPreference(educations, education)) {
                errors.put(educationInputId, "Hakutoiveiden välillä ei saa olla tyhjiä rivejä.");
            }

            learningInstitutions.add(learningInstitution);
            educations.add(education);

        }
        return new ValidationResult(errors);
    }

    private boolean checkBothNullOrTyped(final String learningInstitution, final String education) {
        if ((learningInstitution == null || learningInstitution.isEmpty()) ^ (education == null || education.isEmpty())) {
            return false;
        }
        return true;
    }

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

    private boolean checkEmptyRowBeforeGivenPreference(final List<String> values, final String value) {
        if (value != null && !value.isEmpty() && !values.isEmpty() && values.get(values.size() - 1) == null) {
            return false;
        }
        return true;
    }
}
