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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/7/1210:27 AM}
 * @since 1.1
 */
public class Form extends Titled {

    private transient Navigation navigation = new Navigation("top");

    private transient String firstPhaseId;

    final transient Map<String, Phase> phases = new HashMap<String, Phase>();

    public Form(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
    }

    public Phase getPhase(String phaseId) {
        return phases.get(phaseId);
    }

    private void addPhase(Phase phase, Phase prev) {
        this.phases.put(phase.getId(), phase);
        phase.initChain(prev);
        navigation.addChild(phase.asLink());
    }

    public void init() {
        Phase prev = null;
        for (Element child : children) {
            final Phase child1 = (Phase) child;
            addPhase(child1, prev);
            prev = child1;
            if (firstPhaseId == null) {
                firstPhaseId = child.getId();
            }
            child.init();
        }
    }

    @JsonIgnore
    public Navigation getNavigation() {
        return navigation;
    }

    @JsonIgnore
    public Phase getFirstPhase() {
        return getPhase(firstPhaseId);
    }

    @JsonIgnore
    public Collection<Phase> getPhases() {
        return phases.values();
    }

    @JsonIgnore
    public Element getElementById(final String elementId) {
        Element element = getChildById(this, elementId);
        if (element == null) {
            throw new ResourceNotFoundException("Could not find element " + elementId);
        }
        return element;
    }

    private Element getChildById(final Element element, final String id) {
        if (element.getId().equals(id)) {
            return element;
        }
        Element tmp = null;
        for (Element child : element.getChildren()) {
            tmp = getChildById(child, id);
            if (tmp != null) {
                return tmp;
            }
        }
        return tmp;
    }
}
