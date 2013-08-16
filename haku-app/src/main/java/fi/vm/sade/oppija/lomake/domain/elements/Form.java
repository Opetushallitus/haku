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

package fi.vm.sade.oppija.lomake.domain.elements;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class Form extends Titled {

    private static final long serialVersionUID = 8083152169717295356L;

    public Form(@JsonProperty(value = "id") final String id,
                @JsonProperty(value = "i18nText") final I18nText i18nText) {
        super(id, i18nText);
    }


    @JsonIgnore
    public boolean isFirstChild(final Element phase) {
        return hasChildren() && this.children.get(0).equals(phase);
    }

    @JsonIgnore
    public Element getPreviewPhase() {
        return super.getChildById("esikatselu");
    }
}
