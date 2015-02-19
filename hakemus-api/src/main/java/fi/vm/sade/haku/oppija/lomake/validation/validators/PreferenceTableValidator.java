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
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionGroup;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.util.SpringInjector;
import fi.vm.sade.haku.oppija.lomake.validation.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.I18nBundle;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class PreferenceTableValidator implements Validator {

    private final List<String> learningInstitutionInputIds = new ArrayList<String>();
    private final List<String> educationInputIds = new ArrayList<String>();
    private final List<GroupRestrictionValidator> groupRestrictionValidators = new ArrayList<GroupRestrictionValidator>();

    private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceTableValidator.class);

    private I18nBundleService i18nBundleService;
    private I18nBundle i18nBundle;

    private ApplicationOptionService applicationOptionService;

    public PreferenceTableValidator(final List<String> learningInstitutionInputIds, final List<String> educationInputIds, List<GroupRestrictionValidator> groupRestrictionValidators) {
        Validate.isTrue(learningInstitutionInputIds.size() == educationInputIds.size());
        this.learningInstitutionInputIds.addAll(learningInstitutionInputIds);
        this.educationInputIds.addAll(educationInputIds);
        this.groupRestrictionValidators.addAll(groupRestrictionValidators);
        SpringInjector.injectSpringDependencies(this);
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        List<String> learningInstitutions = new ArrayList<String>();
        List<String> educations = new ArrayList<String>();

        i18nBundle = i18nBundleService.getBundle(validationInput.getApplicationSystemId());

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
                  i18nBundle.get("yleinen.pakollinen"));
            }

            if (!checkUnique(learningInstitutions, educations, learningInstitution, education)) {
                errors.put(educationInputId, i18nBundle.get("hakutoiveet.duplikaatteja"));
            }

            if (!checkEmptyRowBeforeGivenPreference(educations, education)) {
                errors.put(educationInputIds.get(i - 1), i18nBundle.get("hakutoiveet.tyhjia"));
            }

            learningInstitutions.add(learningInstitution);
            educations.add(education);

        }

        errors.putAll(runGroupRestictionValidators(validationInput));

        return new ValidationResult(errors);
    }

    private Map<String, I18nText> runGroupRestictionValidators(final ValidationInput validationInput) {
        final Map<String, I18nText> errors = new HashMap<>();
        if(!groupRestrictionValidators.isEmpty()) {
            final Map<String, SortedSet<ApplicationOptionInfo>> groupToHakukohdeMap = mapGroupsToHakukohde(getApplicationOptionInfo(errors, validationInput));
            for (GroupRestrictionValidator validator : groupRestrictionValidators) {
                if (groupToHakukohdeMap.containsKey(validator.groupId)) {
                    errors.putAll(validator.validate(groupToHakukohdeMap.get(validator.groupId)));
                }
            }
        }
        return errors;
    }

    private SortedSet<ApplicationOptionInfo> getApplicationOptionInfo(Map<String, I18nText> errors, final ValidationInput validationInput) {
        final SortedSet<ApplicationOptionInfo> aoInfos = new TreeSet<>();

        for(String applicationOptionInput: educationInputIds) {
            final String applicationOptionId = validationInput.getValueByKey(applicationOptionInput + "-id");
            if (!Strings.isNullOrEmpty(applicationOptionId)) {
                try {
                    aoInfos.add(new ApplicationOptionInfo(applicationOptionInput,  applicationOptionService.get(applicationOptionId)));
                } catch (RuntimeException e) {
                    LOGGER.error("Error in validation:" + e.toString(), e);
                    errors.put(applicationOptionInput, i18nBundle.get(PreferenceConcreteValidatorImpl.UNKNOWN_ERROR));
                }
            }
        }
        return aoInfos;
    }

    private Map<String, SortedSet<ApplicationOptionInfo>> mapGroupsToHakukohde(SortedSet<ApplicationOptionInfo> applicationOptionInfos) {
        final Map<String, SortedSet<ApplicationOptionInfo>> groupToHakukohdeMap = new HashMap<>();

        for(ApplicationOptionInfo aoInfo: applicationOptionInfos){
            if(aoInfo.ao.getGroups() != null) {
                for(ApplicationOptionGroup group: aoInfo.ao.getGroups()) {
                    getHakukohdeSet(groupToHakukohdeMap, group.oid).add(aoInfo);
                }
            }
        }
        return groupToHakukohdeMap;
    }

    private SortedSet<ApplicationOptionInfo> getHakukohdeSet(Map<String, SortedSet<ApplicationOptionInfo>> groupToHakukohdeMap, String groupId) {
        SortedSet<ApplicationOptionInfo> hakukohdeSet = groupToHakukohdeMap.get(groupId);
        if(hakukohdeSet == null) {
            hakukohdeSet = new TreeSet<>();
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
