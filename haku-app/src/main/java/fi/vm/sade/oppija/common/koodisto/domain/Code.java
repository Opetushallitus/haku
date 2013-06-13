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

package fi.vm.sade.oppija.common.koodisto.domain;

import fi.vm.sade.oppija.lomake.domain.I18nText;

/**
 * @author Hannu Lyytikainen
 */
public class Code {

    private String value;
    private I18nText metadata;

    public Code(String value, I18nText metadata) {
        this.value = value;
        this.metadata = metadata;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public I18nText getMetadata() {
        return metadata;
    }

    public void setMetadata(I18nText metadata) {
        this.metadata = metadata;
    }
}
