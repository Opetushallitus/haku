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

package fi.vm.sade.oppija.haku.domain.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    private transient String firstCategoryId;

    final transient Map<String, Phase> categories = new HashMap<String, Phase>();
    final transient Map<String, Element> elements = new HashMap<String, Element>();

    public Form(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
    }

    public Phase getCategory(String categoryId) {
        return categories.get(categoryId);
    }

    private void addCategory(Phase phase, Phase prev) {
        this.categories.put(phase.getId(), phase);
        phase.initChain(prev);
        navigation.addChild(phase.asLink());
    }

    public void init() {
        Phase prev = null;
        for (Element child : children) {
            child.init(elements, this);
            final Phase child1 = (Phase) child;
            addCategory(child1, prev);
            prev = child1;
            if (firstCategoryId == null) {
                firstCategoryId = child.getId();
            }
        }
    }

    @JsonIgnore
    public Navigation getNavigation() {
        return navigation;
    }

    @JsonIgnore
    public Phase getFirstCategory() {
        return getCategory(firstCategoryId);
    }

    @JsonIgnore
    public Collection<Phase> getCategories() {
        return categories.values();
    }

    @JsonIgnore
    public Element getElementById(final String elementId) {
        return elements.get(elementId);
    }

    @JsonIgnore
    public Phase getVaiheByTeemaId(String teemaId) {
        for (Phase phase : categories.values()) {
            if (!phase.isPreview()) {
                for (Element e : phase.getChildren()) {
                    if (e.getId().equals(teemaId)) {
                        return phase;
                    }
                }
            }
        }
        return null;
    }
}
