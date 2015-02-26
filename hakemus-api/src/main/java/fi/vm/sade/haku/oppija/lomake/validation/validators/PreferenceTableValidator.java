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

import static com.google.common.base.Strings.isNullOrEmpty;

import com.google.common.base.CharMatcher;

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionGroup;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PreferenceTable;
import fi.vm.sade.haku.oppija.lomake.util.SpringInjector;
import fi.vm.sade.haku.oppija.lomake.validation.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.I18nBundle;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class PreferenceTableValidator implements Validator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceTableValidator.class);

    private I18nBundleService i18nBundleService;
    private I18nBundle i18nBundle;

    private ApplicationOptionService applicationOptionService;
    private PreferenceTable table;

    public PreferenceTableValidator(final PreferenceTable table) {
        this.table = table;
        SpringInjector.injectSpringDependencies(this);
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        List<String> learningInstitutionsIds = new ArrayList<String>();
        List<String> educationIds = new ArrayList<String>();

        i18nBundle = i18nBundleService.getBundle(validationInput.getApplicationSystemId());

        final Map<String, I18nText> errors = new HashMap<String, I18nText>();

        for (int i = 0; i < table.getRows().size(); ++i) {
            final PreferenceRow row = table.getRows().get(i);
            String learningInstitutionId = trimInput(validationInput, row.getLearningInstitutionOidInputId());
            String educationId = trimInput(validationInput, row.getEducationOidInputId());
            String educationName = trimInput(validationInput, row.getEducationInputId());
            String learningInstitutionName = trimInput(validationInput, row.getLearningInstitutionInputId());

            if (!checkAllEmptyOrAllNonEmpty(learningInstitutionId, educationId, learningInstitutionName, educationName)) {
                errors.put(isNullOrEmpty(educationId) ? row.getEducationInputId() : row.getLearningInstitutionInputId(),
                  i18nBundle.get("yleinen.pakollinen"));
            }

            if (!checkUnique(learningInstitutionsIds, educationIds, learningInstitutionId, educationId)) {
                errors.put(row.getEducationInputId(), i18nBundle.get("hakutoiveet.duplikaatteja"));
            }

            if (!checkEmptyRowBeforeGivenPreference(educationIds, educationId)) {
                errors.put(table.getRows().get(i - 1).getEducationInputId(), i18nBundle.get("hakutoiveet.tyhjia"));
            }

            learningInstitutionsIds.add(learningInstitutionId);
            educationIds.add(educationId);

        }

        errors.putAll(runGroupRestictionValidators(validationInput));
        return new ValidationResult(errors);
    }

    private String trimInput(final ValidationInput validationInput, String key) {
        String learningInstitutionId = validationInput.getValueByKey(key);
        return isNullOrEmpty(learningInstitutionId) ? null : CharMatcher.WHITESPACE.trimFrom(learningInstitutionId);

    }

    private Map<String, I18nText> runGroupRestictionValidators(final ValidationInput validationInput) {
        final Map<String, I18nText> errors = new HashMap<>();
        if(!table.getGroupRestrictionValidators().isEmpty()) {
            final Map<String, SortedSet<ApplicationOptionInfo>> groupToHakukohdeMap = mapGroupsToHakukohde(getApplicationOptionInfo(errors, validationInput));
            for (GroupRestrictionValidator validator : table.getGroupRestrictionValidators()) {
                if (groupToHakukohdeMap.containsKey(validator.groupId)) {
                    errors.putAll(validator.validate(groupToHakukohdeMap.get(validator.groupId)));
                }
            }
        }
        return errors;
    }

    private SortedSet<ApplicationOptionInfo> getApplicationOptionInfo(Map<String, I18nText> errors, final ValidationInput validationInput) {
        final SortedSet<ApplicationOptionInfo> aoInfos = new TreeSet<>();

        for(PreferenceRow row: table.getRows()) {
            final String applicationOptionId = validationInput.getValueByKey(row.getEducationInputId() + "-id");
            if (!isNullOrEmpty(applicationOptionId)) {
                try {
                    aoInfos.add(new ApplicationOptionInfo(row.getEducationInputId(),  applicationOptionService.get(applicationOptionId)));
                } catch (RuntimeException e) {
                    LOGGER.error("Error in validation:" + e.toString(), e);
                    errors.put(row.getEducationInputId(), i18nBundle.get(PreferenceConcreteValidatorImpl.UNKNOWN_ERROR));
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

    private boolean checkAllEmptyOrAllNonEmpty(String... strings) {
        boolean empty = isNullOrEmpty(strings[0]);
        for (String s : strings) {
            if (isNullOrEmpty(s) != empty) {
                return false;
            }
        }
        return true;
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

        if (!isNullOrEmpty(learningInstitution) && !isNullOrEmpty(education)) {
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
        if (isNullOrEmpty(value) || values.isEmpty()) return true;
        final String lastValue = values.get(values.size() - 1);
        return !isNullOrEmpty(lastValue);
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
