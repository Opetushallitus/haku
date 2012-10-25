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

package fi.vm.sade.oppija.haku.domain.rules;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.questions.Option;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 10/3/123:25 PM}
 * @since 1.1
 */
public class SelectingSubmitRule extends Rule {
    private final Map<String, RegexOptionRule> expressions = new HashMap<String, RegexOptionRule>();
    private final String target;

    public SelectingSubmitRule(@JsonProperty(value = "id") String id, @JsonProperty(value = "target") String target) {
        super(id);
        this.target = target;
    }


    public void addBinding(Element parent, Element child, String s, Option option) {
        getChildById().put(getId(), parent);
        getChildById().put(target, child);
        expressions.put(option.getId(), new RegexOptionRule(s, option));
    }

    public Map<String, RegexOptionRule> getExpressions() {
        return expressions;
    }

    public String getTarget() {
        return target;
    }
}
