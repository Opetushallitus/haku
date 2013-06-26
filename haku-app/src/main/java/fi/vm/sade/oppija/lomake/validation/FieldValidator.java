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

package fi.vm.sade.oppija.lomake.validation;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import org.apache.commons.lang3.Validate;

public abstract class FieldValidator implements Validator {
    protected final String fieldName;
    protected final I18nText errorMessage;
    protected ValidationResult validValidationResult;
    protected ValidationResult invalidValidationResult;

    protected FieldValidator(final String fieldName, final I18nText errorMessage) {
        Validate.notNull(fieldName, "FieldName can't be null");
        Validate.notNull(errorMessage, "ErrorMessage can't be null");
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
        validValidationResult = new ValidationResult();
        invalidValidationResult = new ValidationResult(this.fieldName,
                errorMessage);
    }

    public String getFieldName() {
        return fieldName;
    }

    public I18nText getErrorMessage() {
        return errorMessage;
    }
}
