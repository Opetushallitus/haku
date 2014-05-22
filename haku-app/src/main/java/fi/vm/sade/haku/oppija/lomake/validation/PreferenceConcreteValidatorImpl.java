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

import com.google.common.base.Strings;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author Mikko Majapuro
 */
@Component
@Profile(value = {"default", "devluokka"})
public class PreferenceConcreteValidatorImpl extends PreferenceConcreteValidator {

    public static final String ERROR_STR = "Preference validation error key={}, values={}, applicationOption={}";
    private final ApplicationOptionService applicationOptionService;
    private static final String GENERIC_ERROR = "hakutoiveet.virheellinen.hakutoive";
    private static final String LOP_ERROR = "hakutoiveet.opetuspisteristiriita";
    private static final String CAN_BE_APPLIED_ERROR = "hakutoiveet.eivoihakea";
    private static final String BASE_EDUCATION_ERROR = "hakutoiveet.pohjakoulutusristiriita";
    private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceConcreteValidatorImpl.class);

    @Autowired
    public PreferenceConcreteValidatorImpl(ApplicationOptionService applicationOptionService) {
        this.applicationOptionService = applicationOptionService;
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
                    return createError(validationInput.getElement().getId(), GENERIC_ERROR);
                }
                if (!checkProvider(validationInput, ao)) {
                    return createError(validationInput.getElement().getId(), LOP_ERROR);
                }
                if (!checkApplicationDates(ao)) {
                    return createError(validationInput.getElement().getId(), CAN_BE_APPLIED_ERROR);
                }
                if (!checkEducationDegree(validationInput, ao)) {
                    return createError(validationInput.getElement().getId(), BASE_EDUCATION_ERROR);
                }
            } catch (RuntimeException e) {
                LOGGER.error("validation error", e);
                return createError(validationInput.getElement().getId(), GENERIC_ERROR);
            }
        } else {
            final String opetuspisteIdKey = validationInput.getElement().getId() + "-Opetuspiste-id";
            if (!Strings.isNullOrEmpty(validationInput.getValues().get(opetuspisteIdKey))) {
                return createError(validationInput.getElement().getId(), LOP_ERROR);
            }
        }
        return validationResult;
    }

    private ValidationResult createError(final String key, final String errorKey) {
        return new ValidationResult(key, ElementUtil.createI18NText(errorKey));
    }

    private boolean checkEducationCode(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        final String key = validationInput.getElement().getId() + "-Koulutus-id-educationcode";

        if (validationInput.getValue(key).equals(applicationOption.getEducationCode())) {
            return true;
        }
        LOGGER.error(ERROR_STR, key, validationInput, applicationOption);
        return false;
    }

    private boolean checkProvider(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        boolean isOk = false;

        final String key = validationInput.getElement().getId() + "-Opetuspiste-id";
        if (applicationOption.getProvider().getId().equals(validationInput.getValues().get(key))) {
            isOk = true;
        }

        final String label = validationInput.getElement().getId() + "-Opetuspiste";
        if (applicationOption.getProvider().getName().equals(validationInput.getValue(label))) {
            isOk = isOk && true;
        } else {
            isOk = false;
        }

        if (!isOk) {
            LOGGER.error(ERROR_STR, key, validationInput, applicationOption);
        }
        return isOk;
    }

    private boolean checkAthlete(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        final String key = validationInput.getElement().getId() + "-Koulutus-id-athlete";

        if (Boolean.valueOf(validationInput.getValues().get(key)).booleanValue()
                == (applicationOption.getProvider().isAthleteEducation() || applicationOption.isAthleteEducation())) {
            return true;
        }
        LOGGER.error(ERROR_STR, key, validationInput, applicationOption);
        return false;
    }

    private boolean checkSora(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        final String key = validationInput.getElement().getId() + "-Koulutus-id-sora";
        if (Boolean.valueOf(validationInput.getValues().get(key)).booleanValue()
                == applicationOption.isSora()) {
            return true;
        }
        LOGGER.error(ERROR_STR, key, validationInput, applicationOption);
        return false;
    }

    private boolean checkKaksoistutkinto(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        final String key = validationInput.getElement().getId() + "-Koulutus-id-kaksoistutkinto";
        if (Boolean.valueOf(validationInput.getValues().get(key)).booleanValue() == applicationOption.isKaksoistutkinto()) {
            return true;
        }
        LOGGER.error(ERROR_STR, key, validationInput, applicationOption);
        return false;
    }

    private boolean checkApplicationDates(final ApplicationOption applicationOption) {
        if (applicationOption.isSpecificApplicationDates() && applicationOption.isCanBeApplied()) {
            return true;
        }
        LOGGER.error("Preference validation error {} ", applicationOption);
        return true;
    }

    private boolean checkTeachingLang(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        final String key = validationInput.getElement().getId() + "-Koulutus-id-lang";
        if (applicationOption.getTeachingLanguages().contains(validationInput.getValues().get(key))) {
            return true;
        }
        LOGGER.error(ERROR_STR, key, validationInput, applicationOption);
        return false;
    }

    private boolean checkEducationDegree(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        final String key = "POHJAKOULUTUS";
        if (applicationOption.getRequiredBaseEducations().contains(validationInput.getValues().get(key))) {
            return true;
        }
        return false;
    }

    private boolean checkApplicationSystem(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        if (applicationOption.getProvider().getApplicationSystemIds().contains(validationInput.getApplicationSystemId())) {
            return true;
        }
        LOGGER.error(ERROR_STR, null, validationInput, applicationOption);
        return false;
    }

    private boolean checkAOIdentifier(final ValidationInput validationInput, final ApplicationOption applicationOption) {
        final String key = validationInput.getElement().getId() + "-Koulutus-id-aoIdentifier";
        if (applicationOption.getAoIdentifier().equals(validationInput.getValue(key))) {
            return true;
        }
        LOGGER.error(ERROR_STR, key, validationInput, applicationOption);
        return false;
    }
}
