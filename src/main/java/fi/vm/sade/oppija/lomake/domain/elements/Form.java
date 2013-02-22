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
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Form extends Titled {

    private transient String firstPhaseId;
    private transient String lastPhaseId;

    final transient Map<String, Phase> phases = new HashMap<String, Phase>();

    public Form(@JsonProperty(value = "id") final String id,
                @JsonProperty(value = "i18nText") final I18nText i18nText) {
        super(id, i18nText);
    }

    public Phase getPhase(String phaseId) {
        return phases.get(phaseId);
    }

    private void addPhase(Phase phase, Phase prev) {
        this.phases.put(phase.getId(), phase);
        phase.initChain(prev);
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
            lastPhaseId = child.getId();
        }
    }

    @JsonIgnore
    public Phase getFirstPhase() {
        return getPhase(firstPhaseId);
    }

    @JsonIgnore
    public Phase getLastPhase() {
        return getPhase(lastPhaseId);
    }

    @JsonIgnore
    public Collection<Phase> getPhases() {
        return phases.values();
    }

    @JsonIgnore
    public Element getElementById(final String elementId) {
        Element element = getChildById(this, elementId);
        if (element == null) {
            throw new ResourceNotFoundExceptionRuntime("Could not find element " + elementId);
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
