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
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.validation.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;

import java.util.*;

public class PreferenceTableValidator implements Validator {

    private final List<String> learningInstitutionInputIds = new ArrayList<String>();
    private final List<String> educationInputIds = new ArrayList<String>();
    private final List<GroupRestrictionValidator> groupRestrictionValidators = new ArrayList<GroupRestrictionValidator>();

    private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceTableValidator.class);

    private I18nBundleService i18nBundleService;

    private ApplicationOptionService applicationOptionService;

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

        errors.putAll(runGroupRestictionValidators(validationInput));

        return new ValidationResult(errors);
    }

    private Map<String, I18nText> runGroupRestictionValidators(final ValidationInput validationInput) {
        final Map<String, I18nText> errors = new HashMap<String, I18nText>();
        if(!groupRestrictionValidators.isEmpty()) {
            final Map<String, SortedSet<String>> groupToHakukohdeMap = mapGroupsToHakukohde(getGroupInfo(errors, validationInput));
            for (GroupRestrictionValidator validator : groupRestrictionValidators) {
                if (groupToHakukohdeMap.containsKey(validator.groupId)) {
                    errors.putAll(validator.validate(groupToHakukohdeMap.get(validator.groupId)));
                }
            }
        }
        return errors;
    }

    private Map<String, List<String>> getGroupInfo(Map<String, I18nText> errors, final ValidationInput validationInput) {
        final Map<String, List<String>> hakukohdeGroups = new TreeMap<String, List<String>>();

        for(String applicationOptionInput: educationInputIds) {
            hakukohdeGroups.put(applicationOptionInput, getGroupInfo(errors, validationInput, applicationOptionInput));
        }
        return hakukohdeGroups;
    }

    private List<String> getGroupInfo(Map<String, I18nText> errors, ValidationInput validationInput, String applicationOptionInput) {
        final String applicationOptionId = validationInput.getValueByKey(applicationOptionInput + "-id");
        if (!Strings.isNullOrEmpty(applicationOptionId)) {
            try {
                ApplicationOption applicationOption = applicationOptionService.get(applicationOptionId);
                if(applicationOption.getGroups() != null) {
                    return applicationOption.getGroups();
                }
            } catch (RuntimeException e) {
                LOGGER.error("Error in validation:" + e.toString(), e);
                errors.put(applicationOptionInput, i18nBundleService.getBundle(validationInput.getApplicationSystemId()).get(PreferenceConcreteValidatorImpl.UNKNOWN_ERROR));
            }
        }
        return new ArrayList<String>();
    }

    private Map<String, SortedSet<String>> mapGroupsToHakukohde(Map<String, List<String>> hakukohdeGroups) {
        final Map<String, SortedSet<String>> groupToHakukohdeMap = new HashMap<String, SortedSet<String>>();

        for(String applicationOptionInput: hakukohdeGroups.keySet()){
            for(String groupId: hakukohdeGroups.get(applicationOptionInput)) {
                getHakukohdeSet(groupToHakukohdeMap, groupId).add(applicationOptionInput);
            }
        }
        return groupToHakukohdeMap;
    }

    private SortedSet<String> getHakukohdeSet(Map<String, SortedSet<String>> groupToHakukohdeMap, String groupId) {
        SortedSet<String> hakukohdeSet = groupToHakukohdeMap.get(groupId);
        if(hakukohdeSet == null) {
            hakukohdeSet = new TreeSet<String>();
            groupToHakukohdeMap.put(groupId, hakukohdeSet);
        }
        return hakukohdeSet;
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

    @Autowired
    public void setI18nBundleService(I18nBundleService i18nBundleService) {
        this.i18nBundleService = i18nBundleService;
    }

    @Autowired
    public void setApplicationOptionService(ApplicationOptionService applicationOptionService) {
        this.applicationOptionService = applicationOptionService;
    }
}
