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

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * @author Mikko Majapuro
 */
public class PostOffice implements Serializable {

    private static final long serialVersionUID = -7984081943035919928L;

    final String postcode;
    final I18nText postOffice;

    public PostOffice(@JsonProperty(value = "postcode") final String postcode,
                      @JsonProperty(value = "postOffice") final I18nText i18nText) {
        this.postOffice = i18nText;
        this.postcode = postcode;
    }

    public I18nText getPostOffice() {
        return postOffice;
    }

    public String getPostcode() {
        return postcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostOffice that = (PostOffice) o;

        if (!postcode.equals(that.postcode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return postcode.hashCode();
    }
}
