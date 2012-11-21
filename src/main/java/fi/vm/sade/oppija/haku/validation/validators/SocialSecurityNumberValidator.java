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
package fi.vm.sade.oppija.haku.validation.validators;

import fi.vm.sade.oppija.haku.validation.ValidationResult;
import fi.vm.sade.oppija.haku.validation.Validator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Mikko Majapuro
 */
public class SocialSecurityNumberValidator extends Validator {

    private String nationalityId;
    private final Pattern socialSecurityNumberPattern;
    private static final String FI = "fi";
    private static final String SOCIAL_SECURITY_NUMBER_PATTERN = "([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))";
    private static final String ERROR_MESSAGE = "Suomen kansalaisen on syötettävä henkilötunnus";
    
    public SocialSecurityNumberValidator(final String socialSecurityNumberId, 
            final String nationalityId) {
        this(socialSecurityNumberId, nationalityId, ERROR_MESSAGE, 
                SOCIAL_SECURITY_NUMBER_PATTERN);
    }
    
    public SocialSecurityNumberValidator(final String socialSecurityNumberId, 
            final String nationalityId, final String errorMessage, 
            final String socialSecurityNumberPattern) {
        super(socialSecurityNumberId, errorMessage);
        this.nationalityId = nationalityId;
        this.socialSecurityNumberPattern = Pattern.compile(socialSecurityNumberPattern);
    }
    
    @Override
    public ValidationResult validate(Map<String, String> values) {
        String socialSecurityNumber = values.get(fieldName);
        String nationality = values.get(nationalityId);
        ValidationResult validationResult = new ValidationResult();
        if (socialSecurityNumber != null && nationality != null) {
            Matcher matcher = socialSecurityNumberPattern.matcher(socialSecurityNumber);
            if (nationality.equalsIgnoreCase(FI) && !matcher.matches()) {
                validationResult = new ValidationResult(fieldName, errorMessage);
            }
        }
        return validationResult;
    }
    
}
