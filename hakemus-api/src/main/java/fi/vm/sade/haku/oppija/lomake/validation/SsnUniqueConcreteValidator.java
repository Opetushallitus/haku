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

import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.I18nBundle;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mikko Majapuro
 */
@Component
public class SsnUniqueConcreteValidator implements Validator {

    private final ApplicationDAO applicationDAO;
    private final I18nBundleService i18nBundleService;
    private final Pattern socialSecurityNumberPattern;
    private static final String SOCIAL_SECURITY_NUMBER_PATTERN = "([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))";

    @Autowired
    public SsnUniqueConcreteValidator(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO, final I18nBundleService i18nBundleService) {
        this.applicationDAO = applicationDAO;
        this.i18nBundleService = i18nBundleService;
        this.socialSecurityNumberPattern = Pattern.compile(SOCIAL_SECURITY_NUMBER_PATTERN);
    }

    @Override
    public ValidationResult validate(ValidationInput validationInput) {
        return checkIfExistsBySocialSecurityNumber(validationInput);
    }

    private ValidationResult checkIfExistsBySocialSecurityNumber(final ValidationInput validationInput) {
        ValidationResult validationResult = new ValidationResult();
        String ssn= validationInput.getValue();
        if (ssn != null && validationInput.getApplicationOid() == null) {
            Matcher matcher = socialSecurityNumberPattern.matcher(ssn);
            if (matcher.matches() && this.applicationDAO.checkIfExistsBySocialSecurityNumber(validationInput.getApplicationSystemId(), ssn)) {
                ValidationResult result = new ValidationResult(validationInput.getFieldName(),
                  i18nBundleService.getBundle(validationInput.getApplicationSystemId()).get("henkilotiedot.hetuKaytetty"));
                return new ValidationResult(Arrays.asList(new ValidationResult[]{validationResult, result}));
            }
        }
        return validationResult;
    }
}
