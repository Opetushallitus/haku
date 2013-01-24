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

package fi.vm.sade.oppija.lomake.domain.rules;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author jteuho
 */
public class AddElementRule extends Rule {

    private final String relatedElementId;
    private final String text;

    public AddElementRule(@JsonProperty(value = "id") String id,
            @JsonProperty(value = "relatedElementId") String relatedElementId,
            @JsonProperty(value = "text") String text) {
        super(id);
        this.text = text;
        this.relatedElementId = relatedElementId;
    }

    public String getText() {
        return text;
    }
    
    public String getRelatedElementId() {
        return relatedElementId;
    }

}
