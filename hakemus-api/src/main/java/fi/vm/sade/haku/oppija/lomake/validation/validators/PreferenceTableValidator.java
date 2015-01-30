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

package fi.vm.sade.haku.oppija.lomake.validation.validators;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.validation.GroupRestrictionValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.oppija.lomake.validation.Validator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferenceTableValidator implements Validator {

    public final List<String> learningInstitutionInputIds = new ArrayList<String>();
    public final List<String> educationInputIds = new ArrayList<String>();
    public final List<GroupRestrictionValidator> groupRestrictionValidators = new ArrayList<GroupRestrictionValidator>();

    public PreferenceTableValidator(final List<String> learningInstitutionInputIds, final List<String> educationInputIds, List<GroupRestrictionValidator> groupRestrictionValidators) {
        Validate.isTrue(learningInstitutionInputIds.size() == educationInputIds.size());
        this.learningInstitutionInputIds.addAll(learningInstitutionInputIds);
        this.educationInputIds.addAll(educationInputIds);
        this.groupRestrictionValidators.addAll(groupRestrictionValidators);
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        List<String> learningInstitutions = new ArrayList<String>();
        List<String> educations = new ArrayList<String>();

        final Map<String, I18nText> errors = new HashMap<String, I18nText>();

        for (int i = 0; i < this.learningInstitutionInputIds.size(); ++i) {
            String learningInstitutionInputId = learningInstitutionInputIds.get(i);
            String educationInputId = educationInputIds.get(i);
            String learningInstitution = validationInput.getValueByKey(learningInstitutionInputId);
            learningInstitution = Strings.isNullOrEmpty(learningInstitution) ? null : CharMatcher.WHITESPACE.trimFrom(learningInstitution);
            String education = validationInput.getValueByKey(educationInputId);
            education = Strings.isNullOrEmpty(education) ? null : CharMatcher.WHITESPACE.trimFrom(education);

            if (!checkBothNullOrTyped(learningInstitution, education)) {
                errors.put(Strings.isNullOrEmpty(education) ? educationInputId : learningInstitutionInputId,
                        ElementUtil.createI18NText("yleinen.pakollinen"));
            }

            if (!checkUnique(learningInstitutions, educations, learningInstitution, education)) {
                errors.put(educationInputId, ElementUtil.createI18NText("hakutoiveet.duplikaatteja"));
            }

            if (!checkEmptyRowBeforeGivenPreference(educations, education)) {
                errors.put(educationInputIds.get(i - 1), ElementUtil.createI18NText("hakutoiveet.tyhjia"));
            }

            learningInstitutions.add(learningInstitution);
            educations.add(education);

        }

        errors.putAll(runGroupRestictionValidators());

        return new ValidationResult(errors);
    }

    private Map<String, I18nText> runGroupRestictionValidators() {
        final Map<String, List<String>> aoGroups = new HashMap<String, List<String>>();
        final Map<String, I18nText> errors = new HashMap<String, I18nText>();
        for(GroupRestrictionValidator validator: groupRestrictionValidators) {
            errors.putAll(validator.validate(aoGroups));
        }
        return errors;
    }

    /**
     * Checks that the both preference input fields has values or the both is null
     *
     * @param learningInstitution learning institution input value
     * @param education           education input value
     * @return true if valid, false otherwise
     */
    private boolean checkBothNullOrTyped(final String learningInstitution, final String education) {
        return !(Strings.isNullOrEmpty(learningInstitution) ^ Strings.isNullOrEmpty(education));
    }

    /**
     * Checks that the given preference is unique in a preference table
     *
     * @param learningInstitutions
     * @param educations
     * @param learningInstitution  learning institute input value
     * @param education            education input value   @return true if valid, false otherwise
     */
    private boolean checkUnique(List<String> learningInstitutions, List<String> educations,
                                final String learningInstitution, final String education) {

        if (!Strings.isNullOrEmpty(learningInstitution) && !Strings.isNullOrEmpty(education)) {
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
        return !(!Strings.isNullOrEmpty(value) && !values.isEmpty() && (Strings.isNullOrEmpty(values.get(values.size() - 1))));
    }
}
