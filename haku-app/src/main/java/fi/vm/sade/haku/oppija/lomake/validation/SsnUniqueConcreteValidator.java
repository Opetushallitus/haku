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
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
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
    private final Pattern socialSecurityNumberPattern;
    private static final String SOCIAL_SECURITY_NUMBER_PATTERN = "([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))";

    @Autowired
    public SsnUniqueConcreteValidator(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO) {
        this.applicationDAO = applicationDAO;

        this.socialSecurityNumberPattern = Pattern.compile(SOCIAL_SECURITY_NUMBER_PATTERN);
    }

    @Override
    public ValidationResult validate(ValidationInput validationInput) {
        return checkIfExistsBySocialSecurityNumber(validationInput.getApplicationSystemId(),
                validationInput.getValue(SocialSecurityNumber.HENKILOTUNNUS),
                validationInput.getApplicationOid());
    }

    private ValidationResult checkIfExistsBySocialSecurityNumber(String asId, String ssn, String applicationOid) {
        ValidationResult validationResult = new ValidationResult();
        if (ssn != null && applicationOid == null) {
            Matcher matcher = socialSecurityNumberPattern.matcher(ssn);
            if (matcher.matches() && this.applicationDAO.checkIfExistsBySocialSecurityNumber(asId, ssn)) {
                ValidationResult result = new ValidationResult("Henkilotunnus",
                        ElementUtil.createI18NText("henkilotiedot.hetuKaytetty", "form_common"));
                return new ValidationResult(Arrays.asList(new ValidationResult[]{validationResult, result}));
            }
        }
        return validationResult;
    }
}
