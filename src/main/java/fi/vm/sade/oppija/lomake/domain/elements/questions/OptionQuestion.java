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

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.lomake.domain.elements.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class OptionQuestion extends Question {

    private final List<Option> options = new ArrayList<Option>();

    protected OptionQuestion(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
    }

    public void addOption(final String id, final String value, final String title) {
        this.options.add(new Option(this.getId() + ID_DELIMITER + id, value, title));
    }

    @Override
    public void init(Map<String, Element> elements, Element parent) {
        super.init(elements, parent);
        for (Option option : options) {
            option.init(elements, this);
        }
    }

    public void addOption(final String id, final String value, final String title, final String help) {
        Option opt = new Option(this.getId() + ID_DELIMITER + id, value, title);
        opt.setHelp(help);
        this.options.add(opt);
    }

    public List<Option> getOptions() {
        return options;
    }
}


