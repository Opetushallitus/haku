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

package fi.vm.sade.oppija.haku.validation;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationResult {
    private final Map<String, String> errors;

    public ValidationResult(final Map<String, String> errors) {
        this.errors = Collections.unmodifiableMap(errors);
    }

    public ValidationResult() {
        this.errors = Collections.unmodifiableMap(Collections.<String, String>emptyMap());
    }

    public ValidationResult(final String key, final String error) {
        HashMap<String, String> errorMessages = new HashMap<String, String>();
        errorMessages.put(key, error);
        this.errors = Collections.unmodifiableMap(errorMessages);
    }

    public ValidationResult(final List<ValidationResult> validationResults) {
        HashMap<String, String> errorMessages = new HashMap<String, String>();
        for (ValidationResult validationResult : validationResults) {
            if (validationResult.hasErrors()) {
                errorMessages.putAll(validationResult.getErrorMessages());
            }
        }
        this.errors = Collections.unmodifiableMap(errorMessages);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public Map<String, String> getErrorMessages() {
        return errors;
    }

}
