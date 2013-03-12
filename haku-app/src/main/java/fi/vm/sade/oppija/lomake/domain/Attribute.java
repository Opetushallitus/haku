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

package fi.vm.sade.oppija.lomake.domain;

import org.apache.commons.lang.Validate;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Attribute implements Serializable {

    private static final long serialVersionUID = 3012830534204489765L;

    private final String key;
    private final String value;

    public Attribute(@JsonProperty(value = "key") final String key, @JsonProperty(value = "value") final String value) {
        Validate.notNull(key, "key is null");
        Validate.notNull(value, "value is null");
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @JsonIgnore
    public String getAsString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getKey());
        builder.append("=\"");
        builder.append(getValue());
        builder.append("\" ");
        return builder.toString();
    }
}
