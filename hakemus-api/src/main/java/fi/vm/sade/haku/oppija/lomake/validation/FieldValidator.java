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

package fi.vm.sade.haku.oppija.lomake.validation;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.util.SpringInjector;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.I18nBundle;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;

public abstract class FieldValidator implements Validator {
    private String errorMessageKey;

    @Transient
    private I18nBundleService i18nBundleService;
    @Transient
    private I18nBundle i18nBundle;
    @Transient
    protected ValidationResult validValidationResult;

    protected FieldValidator(final String errorMessageKey) {
        Validate.notNull(errorMessageKey, "ErrorMessageKey can't be null");
        this.errorMessageKey = errorMessageKey;
        validValidationResult = new ValidationResult();
        SpringInjector.injectSpringDependencies(this);
    }

    public ValidationResult getInvalidValidationResult(final ValidationInput validationInput) {
        return new ValidationResult(validationInput.getFieldName(), getI18Text(errorMessageKey,
          validationInput.getApplicationSystemId()));
    }

    public String getErrorMessageKey() {
        return errorMessageKey;
    }

    public void setErrorMessageKey(String errorMessageKey) {
        this.errorMessageKey = errorMessageKey;
    }

    @Autowired
    public void setI18nBundleService(I18nBundleService i18nBundleService) {
        this.i18nBundleService = i18nBundleService;
    }

    protected I18nText getI18Text(final String key, final String applicationSystemId){
        if (null == i18nBundle) {
            if (null == i18nBundleService){
                return ElementUtil.createI18NAsIs(key);
            }
            i18nBundle = i18nBundleService.getBundle(applicationSystemId);
        }
        return i18nBundle.get(key);
    }
}
