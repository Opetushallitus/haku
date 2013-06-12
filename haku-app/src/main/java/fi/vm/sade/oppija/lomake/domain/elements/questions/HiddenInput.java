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

package fi.vm.sade.oppija.lomake.domain.elements.questions;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Hidden element. Renders into an input, a fixed value can be set beforehand.
 *
 * @author Hannu Lyytikainen
 */
public class HiddenInput extends Question {

    private String value;

    public HiddenInput(@JsonProperty String id, @JsonProperty String value) {
        super(id, null);
        this.value = value;
        this.addAttribute("hidden", "true");
    }

    public String getValue() {
        return value;
    }
}
