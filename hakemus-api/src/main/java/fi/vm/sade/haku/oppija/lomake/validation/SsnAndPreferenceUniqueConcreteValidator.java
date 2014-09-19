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
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationFilterParameters;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.PREFERENCE_ID;

/**
 * @author Mikko Majapuro
 */
@Component
public class SsnAndPreferenceUniqueConcreteValidator implements Validator {

    private final ApplicationDAO applicationDAO;
    private final ApplicationSystemService applicationSystemService;
    private final Pattern socialSecurityNumberPattern;
    private static final String SOCIAL_SECURITY_NUMBER_PATTERN = "([0-9]{6}.[0-9]{3}([0-9]|[a-z]|[A-Z]))";
    private final String preferenceKey;

    @Autowired
    public SsnAndPreferenceUniqueConcreteValidator(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO,
                                                   ApplicationSystemService applicationSystemService) {
        this.applicationDAO = applicationDAO;
        this.applicationSystemService = applicationSystemService;
        this.socialSecurityNumberPattern = Pattern.compile(SOCIAL_SECURITY_NUMBER_PATTERN);
        this.preferenceKey = String.format(PREFERENCE_ID, 1);
    }

    @Override
    public ValidationResult validate(ValidationInput validationInput) {
        return checkIfExistsBySocialSecurityNumberAndAo(
                validationInput.getApplicationSystemId(),
                validationInput.getValueByKey(OppijaConstants.ELEMENT_ID_SOCIAL_SECURITY_NUMBER),
                validationInput.getApplicationOid(),
                validationInput.getValueByKey(preferenceKey),
                validationInput.getElement().getId());
    }

    private ValidationResult checkIfExistsBySocialSecurityNumberAndAo(String asId, String ssn, String applicationOid, String aoId, String elementId) {
        ValidationResult validationResult = new ValidationResult();
        ApplicationSystem as = applicationSystemService.getApplicationSystem(asId);
        ApplicationFilterParameters filterParams =
                new ApplicationFilterParameters(as.getMaxApplicationOptions(), null, null, null);
        if (!Strings.isNullOrEmpty(ssn) && Strings.isNullOrEmpty(applicationOid) && !Strings.isNullOrEmpty(aoId)) {
            Matcher matcher = socialSecurityNumberPattern.matcher(ssn);
            if (matcher.matches() && this.applicationDAO.checkIfExistsBySocialSecurityNumberAndAo(filterParams, asId, ssn, aoId)) {
                ValidationResult result = new ValidationResult(elementId, ElementUtil.createI18NText("henkilotiedot.hetuKaytetty"));
                return new ValidationResult(Arrays.asList(new ValidationResult[]{validationResult, result}));
            }
        }
        return validationResult;
    }
}
