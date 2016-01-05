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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationResult {
    private final Map<String, I18nText> errors;
    // expired is set by the validation when the application deadline has passed for an application
    // If true upon application submission this will throw an exception that renders static error page
    // from haku-app/src/main/webapp/WEB-INF/jsp/error/errorApplicationDeadlineExpired.jsp
    private boolean expired;

    public ValidationResult(final Map<String, I18nText> errors) {
        this.errors = new HashMap<String, I18nText>();
        this.errors.putAll(errors);
    }

    public ValidationResult() {
        this.errors = Collections.emptyMap();
    }

    public ValidationResult(final String key, final I18nText error) {
        this.errors = new HashMap<String, I18nText>();
        this.errors.put(key, error);
    }

    public ValidationResult(final List<ValidationResult> validationResults) {
        this.errors = new HashMap<String, I18nText>();
        for (ValidationResult validationResult : validationResults) {
            if (validationResult.hasErrors()) {
                errors.putAll(validationResult.getErrorMessages());
            }
            if (validationResult.isExpired()) {
                this.expired = true;
            }
        }
    }

    public void setExpired(boolean value) {
        this.expired = value;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean isExpired() { return this.expired; }

    public void addValidationResult(ValidationResult validationResult) {
        this.errors.putAll(validationResult.getErrorMessages());
    }

    public Map<String, I18nText> getErrorMessages() {
        return Collections.unmodifiableMap(this.errors);
    }

}
