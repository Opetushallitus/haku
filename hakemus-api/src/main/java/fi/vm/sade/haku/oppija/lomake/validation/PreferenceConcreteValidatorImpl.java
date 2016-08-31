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

package fi.vm.sade.haku.oppija.lomake.validation;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Component
public class PreferenceConcreteValidatorImpl extends PreferenceConcreteValidator {

    private final ApplicationOptionService applicationOptionService;
    private final ApplicationSystemService applicationSystemService;
    private final I18nBundleService i18nBundleService;
    public static final String UNKNOWN_ERROR = "form.odottamaton.virhe";
    private static final String GENERIC_ERROR = "hakutoiveet.virheellinen.hakutoive";
    private static final String LOP_ERROR = "hakutoiveet.opetuspisteristiriita";
    private static final String CAN_BE_APPLIED_ERROR = "hakutoiveet.eivoihakea";
    private static final String BASE_EDUCATION_ERROR = "hakutoiveet.pohjakoulutusristiriita";
    private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceConcreteValidatorImpl.class);

    @Autowired
    public PreferenceConcreteValidatorImpl(ApplicationOptionService applicationOptionService,
                                           ApplicationSystemService applicationSystemService,
                                           I18nBundleService i18nBundleService) {
        this.applicationOptionService = applicationOptionService;
        this.applicationSystemService = applicationSystemService;
        this.i18nBundleService = i18nBundleService;
    }

    @Override
    public ValidationResult validate(ValidationInput validationInput) {

        ValidationResult validationResult = new ValidationResult();
        final String key = validationInput.getElement().getId() + "-Koulutus-id";
        final String aoId = validationInput.getValues().get(key);
        if (!Strings.isNullOrEmpty(aoId)) {
            try {
                ApplicationOption ao = applicationOptionService.get(aoId);
                if (!checkAthlete(validationInput, ao) ||
                        !checkSora(validationInput, ao) ||
                        !checkEducationCode(validationInput, ao) ||
                        !checkTeachingLang(validationInput, ao) ||
                        !checkApplicationSystem(validationInput, ao) ||
                        !checkAOIdentifier(validationInput, ao) ||
                        !checkKaksoistutkinto(validationInput, ao)) {
                    return createError(validationInput.getElement().getId(), GENERIC_ERROR, validationInput.getApplicationSystemId());
                }
                // Must be checked against all langs
                if (!(checkProvider(validationInput, aoId))) {
                    return createError(validationInput.getElement().getId(), LOP_ERROR, validationInput.getApplicationSystemId());
                }
                if (!checkApplicationDates(validationInput, ao)) {
                    ValidationResult result = createError(validationInput.getElement().getId(), CAN_BE_APPLIED_ERROR, validationInput.getApplicationSystemId());
                    result.setExpired(true);
                    return result;
                }
                if (!checkEducationDegree(validationInput, ao)) {
                    return createError(validationInput.getElement().getId(), BASE_EDUCATION_ERROR, validationInput.getApplicationSystemId());
                }
            } catch (RuntimeException e) {
                LOGGER.error("Error in validation: " + e.toString(), e);
                return createError(validationInput.getElement().getId(), UNKNOWN_ERROR, validationInput.getApplicationSystemId());
            }
        } else {
            final String opetuspisteIdKey = validationInput.getElement().getId() + "-Opetuspiste-id";
            if (!Strings.isNullOrEmpty(validationInput.getValues().get(opetuspisteIdKey))) {
                return createError(validationInput.getElement().getId(), LOP_ERROR, validationInput.getApplicationSystemId());
            }
        }
        return validationResult;
    }

    private ValidationResult createError(final String key, final String errorKey, final String applicationSystemId) {
        return new ValidationResult(key, i18nBundleService.getBundle(applicationSystemId).get(errorKey));
    }

    private boolean checkEducationCode(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        if (!ValidationInput.ValidationContext.applicant_submit.equals(validationInput.getValidationContext())) {
            return true;
        }
        final String key = validationInput.getElement().getId() + "-Koulutus-id-educationcode";
        final String value = validationInput.getValueByKey(key);
        if (StringUtils.trimToEmpty(value).equals(applicationOption.getEducationCode())) {
            return true;
        }
        LOGGER.error("Education Code validation failed for {}. Application: {}. Expected: {}. Got: {}", applicationOption,
                validationInput.getApplicationOid(), applicationOption.getEducationCode(), value);
        return false;
    }

    private boolean checkProvider(final ValidationInput validationInput, final String aoId) {
        if (validationInput.getValidationContext() != ValidationInput.ValidationContext.applicant_submit) {
            return true;
        }

        ApplicationOption applicationOption = applicationOptionService.get(aoId);
        boolean isOk = false;

        final String key = validationInput.getElement().getId() + "-Opetuspiste-id";
        if (applicationOption.getProvider().getId().equals(validationInput.getValues().get(key))) {
            isOk = true;
        }

        if (!isOk) {
            LOGGER.error("Provider ID validation failed for {}. Application: {}. Expected {}. Got: {}",
                    applicationOption,
                    validationInput.getApplicationOid(),
                    applicationOption.getProvider().getId(),
                    validationInput.getValues().get(key));
        }

        final String label = validationInput.getElement().getId() + "-Opetuspiste";
        List<String> names = new ArrayList<String>(3);
        for (String lang : new String[] {"fi", "sv", "en"}) {
            ApplicationOption ao = applicationOptionService.get(aoId, lang);
            String name = "(not found: "+lang+")";
            if (ao != null) {
                name = ao.getProvider().getName();
            }
            names.add(name);
        }
        boolean nameOk = false;
        for (String name : names) {
            nameOk = nameOk || name.equals(validationInput.getValueByKey(label));
        }
        if (!nameOk) {
            isOk = false;
            LOGGER.error("Provider name validation failed for {}. Application: {}. Expected one of [{}]. Got: {}",
                    applicationOption,
                    validationInput.getApplicationOid(),
                    Joiner.on(",").join(names),
                    validationInput.getValueByKey(label));
        }

        return isOk;
    }

    private boolean checkAthlete(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        final String key = validationInput.getElement().getId() + "-Koulutus-id-athlete";

        boolean expected = (applicationOption.getProvider().isAthleteEducation() || applicationOption.isAthleteEducation());
        if (Boolean.valueOf(validationInput.getValues().get(key)).booleanValue()
                == expected) {
            return true;
        }
        LOGGER.error("Athlete education validation failed for {}. Application: {}. Expected: {}. Got: {}", applicationOption,
                validationInput.getApplicationOid(),
                expected, validationInput.getValues().get(key));
        return false;
    }

    private boolean checkSora(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        final String key = validationInput.getElement().getId() + "-Koulutus-id-sora";
        if (Boolean.valueOf(validationInput.getValues().get(key)).booleanValue()
                == applicationOption.isSora()) {
            return true;
        }
        LOGGER.error("Sora validation failed for {}. Application: {}. Expected: {}. Got: {}", applicationOption,
                validationInput.getApplicationOid(), applicationOption.isSora(),
                validationInput.getValues().get(key));
        return false;
    }

    private boolean checkKaksoistutkinto(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        final String key = validationInput.getElement().getId() + "-Koulutus-id-kaksoistutkinto";
        if (Boolean.valueOf(validationInput.getValues().get(key)).booleanValue() == applicationOption.isKaksoistutkinto()) {
            return true;
        }
        LOGGER.error("Double degree validation failed for {}. Application: {}. Expected: {}. Got: {}", applicationOption,
                validationInput.getApplicationOid(),
                applicationOption.isKaksoistutkinto(), validationInput.getValues().get(key));
        return false;
    }

    private boolean checkApplicationDates(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        if (validationInput.getValidationContext() == ValidationInput.ValidationContext.applicant_submit && !applicationOption.isCanBeApplied()) {
            LOGGER.error("Application date validation failed for {}. Application: {}", applicationOption, validationInput.getApplicationOid());
            return false;
        }
        return true;
    }

    private boolean checkTeachingLang(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        final String key = validationInput.getElement().getId() + "-Koulutus-id-lang";
        if (applicationOption.getTeachingLanguages().contains(validationInput.getValues().get(key))) {
            return true;
        }
        LOGGER.error("Language validation failed for {}. Application: {}. '{}' not in allowed languages", applicationOption,
                validationInput.getApplicationOid(), validationInput.getValues().get(key));
        return false;
    }

    private boolean checkEducationDegree(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        ApplicationSystem as = applicationSystemService.getApplicationSystem(validationInput.getApplicationSystemId());
        if (as.baseEducationDoesNotRestrictApplicationOptions()) {
            return true;
        }
        final String key = "POHJAKOULUTUS";
        if (applicationOption.getRequiredBaseEducations().contains(validationInput.getValues().get(key))) {
            return true;
        }
        LOGGER.error("Base education validation failed for {}. Application: {}. '{}' not in allowed educations" , applicationOption,
                validationInput.getApplicationOid(),
                validationInput.getValues().get(key));
        return false;
    }

    private boolean checkApplicationSystem(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        if (applicationOption.getApplicationSystemId().equals(validationInput.getApplicationSystemId())) {
            return true;
        }
        LOGGER.error("Application system validation failed for {}. Allowed id: {}. Application system id: {}",
                applicationOption, applicationOption.getApplicationSystemId(), validationInput.getApplicationSystemId());
        return false;
    }

    private boolean checkAOIdentifier(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        final String key = validationInput.getElement().getId() + "-Koulutus-id-aoIdentifier";
        final String value = StringUtil.safeToString(validationInput.getValueByKey(key));
        final String expected = StringUtil.safeToString(applicationOption.getAoIdentifier());
        if (expected.equals(value)) {
            return true;
        }
        LOGGER.error("Application option identifier validation failed for {}. Application: {}. Expected: {}. Got: {}", applicationOption,
                validationInput.getApplicationOid(), expected, value);
        return false;
    }
}
