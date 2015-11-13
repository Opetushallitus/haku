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

import fi.vm.sade.haku.oppija.lomake.validation.validators.EmailUniqueValidator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.PreferenceValidator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.SsnAndPreferenceUniqueValidator;
import fi.vm.sade.haku.oppija.lomake.validation.validators.SsnUniqueValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Mikko Majapuro
 */
@Component
public class ValidatorFactory {

    private final SsnUniqueConcreteValidator ssnUniqueConcreteValidator;
    private final SsnAndPreferenceUniqueConcreteValidator ssnAndPreferenceUniqueConcreteValidator;
    private final PreferenceConcreteValidator preferenceConcreteValidator;
    private final EmailUniqueConcreteValidator emailUniqueConcreteValidator;

    @Autowired
    public ValidatorFactory(SsnUniqueConcreteValidator ssnUniqueConcreteValidator,
                            SsnAndPreferenceUniqueConcreteValidator ssnAndPreferenceUniqueConcreteValidator,
                            PreferenceConcreteValidator preferenceConcreteValidator,
                            EmailUniqueConcreteValidator emailUniqueConcreteValidator) {
        this.ssnUniqueConcreteValidator = ssnUniqueConcreteValidator;
        this.ssnAndPreferenceUniqueConcreteValidator = ssnAndPreferenceUniqueConcreteValidator;
        this.preferenceConcreteValidator = preferenceConcreteValidator;
        this.emailUniqueConcreteValidator = emailUniqueConcreteValidator;
    }

    public Validator buildValidator(final Validator validator) {
        Class validatorClass = validator.getClass();
        if (SsnUniqueValidator.class.isAssignableFrom(validatorClass)) {
            return ssnUniqueConcreteValidator;
        } else if (SsnAndPreferenceUniqueValidator.class.isAssignableFrom(validatorClass)) {
            return ssnAndPreferenceUniqueConcreteValidator;
        } else if (PreferenceValidator.class.isAssignableFrom(validatorClass)) {
            return preferenceConcreteValidator;
        } else if (EmailUniqueValidator.class.isAssignableFrom(validatorClass)) {
            return emailUniqueConcreteValidator;
        }
        return validator;
    }
}
