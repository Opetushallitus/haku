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

import fi.vm.sade.oppija.lomake.validation.validators.*;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ContainedInOtherFieldValidator.class),
        @JsonSubTypes.Type(value = FunctionalValidator.class),
        @JsonSubTypes.Type(value = PreferenceTableValidator.class),
        @JsonSubTypes.Type(value = RegexFieldValidator.class),
        @JsonSubTypes.Type(value = RequiredFieldValidator.class),
        @JsonSubTypes.Type(value = SocialSecurityNumberFieldValidator.class),
        @JsonSubTypes.Type(value = UniqValuesValidator.class),
        @JsonSubTypes.Type(value = ValueSetValidator.class)
}
)
public interface Validator {
    ValidationResult validate(final Map<String, String> values);
}
