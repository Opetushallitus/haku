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
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EmailUniqueConcreteValidator implements Validator {

    private final ApplicationDAO applicationDAO;
    private final I18nBundleService i18nBundleService;

    @Autowired
    public EmailUniqueConcreteValidator(@Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO,
                                        I18nBundleService i18nBundleService) {
        this.applicationDAO = applicationDAO;
        this.i18nBundleService = i18nBundleService;
    }

    @Override
    public ValidationResult validate(ValidationInput validationInput) {
        if (validationInput.getApplicationOid() == null // validate only when submitting a new application
            && applicationDAO.checkIfExistsByEmail(validationInput.getApplicationSystemId(),
                lower(validationInput.getValueByKey(OppijaConstants.ELEMENT_ID_EMAIL)))) {
            I18nText texts = i18nBundleService.getBundle(validationInput.getApplicationSystemId()).get("form.email.duplicate");
            return new ValidationResult(OppijaConstants.ELEMENT_ID_EMAIL, texts);
        }
        return new ValidationResult();
    }

    private String lower(String email) {
        if (!Strings.isNullOrEmpty(email)) {
            return email.toLowerCase();
        }
        return null;
    }
}
