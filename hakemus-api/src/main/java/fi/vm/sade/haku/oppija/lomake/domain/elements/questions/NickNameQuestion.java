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

package fi.vm.sade.haku.oppija.lomake.domain.elements.questions;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import org.codehaus.jackson.annotate.JsonProperty;

public class NickNameQuestion extends TextQuestion {

    private static final long serialVersionUID = -4941045359062359811L;
    private String firstName;

    public NickNameQuestion(@JsonProperty(value = "id") final String id,
                            @JsonProperty(value = "i18nText") final I18nText i18nText,
                            @JsonProperty(value = "firstName") final String firstName) {
        super(id, i18nText);
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }
}
