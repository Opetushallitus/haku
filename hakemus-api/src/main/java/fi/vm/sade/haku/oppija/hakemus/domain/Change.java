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
package fi.vm.sade.haku.oppija.hakemus.domain;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Change implements Serializable {
    private Date modified;
    private String modifier;
    private String reason;
    private List<Map<String, String>> changes;

    @JsonCreator
    public Change(@JsonProperty(value = "modified") final Date modified,
                  @JsonProperty(value = "modifier") final String modifier,
                  @JsonProperty(value = "reason") final String reason,
                  @JsonProperty(value = "changes") final List<Map<String, String>> changes) {
        this.modified = modified;
        this.modifier = modifier;
        this.reason = reason;
        this.changes = changes;
    }

    public Date getModified() {
        return modified;
    }

    public String getModifier() {
        return modifier;
    }

    public String getReason() {
        return reason;
    }

    public List<Map<String, String>> getChanges() {
        return changes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Change)) return false;

        Change change = (Change) o;

        if (!modified.equals(change.modified)) return false;
        if (!modifier.equals(change.modifier)) return false;
        if (!reason.equals(change.reason)) return false;
        if (!changes.equals(change.changes)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = modified != null ? modified.hashCode() : 0;
        result = 31 * result + (modifier != null ? modifier.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (changes != null ? changes.hashCode() : 0);
        return result;
    }
}
