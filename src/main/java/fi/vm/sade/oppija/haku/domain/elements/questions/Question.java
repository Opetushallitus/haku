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

package fi.vm.sade.oppija.haku.domain.elements.questions;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.Titled;

/**
 * @author jukka
 * @version 9/7/1210:37 AM}
 * @since 1.1
 */
public abstract class Question extends Titled {

    // verbose help text that is rendered in a separate help window
    private String verboseHelp;

    protected Question(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title) {
        super(id, title);
        addAttribute("id", id);
        addAttribute("name", id);
    }

    public String getVerboseHelp() {
        return verboseHelp;
    }

    public void setVerboseHelp(String verboseHelp) {
        this.verboseHelp = verboseHelp;
    }
}
