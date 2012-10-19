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
import java.util.List;
import java.util.Map;

/**
 * @author jukka
 * @version 9/7/1210:27 AM}
 * @since 1.1
 */
public class Form extends Titled {

    private transient Navigation navigation = new Navigation("top");

    private transient String firstCategoryId;

    final transient Map<String, Vaihe> categories = new HashMap<String, Vaihe>();

    public Form(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
    }

    public Vaihe getCategory(String categoryId) {
        return categories.get(categoryId);
    }

    private void addCategory(Vaihe vaihe, Vaihe prev) {
        this.categories.put(vaihe.getId(), vaihe);
        vaihe.initChain(prev);
        navigation.addChild(vaihe.asLink());
    }

    public void init() {
        Vaihe prev = null;
        for (Element child : children) {
            if (child instanceof Vaihe) {
                final Vaihe child1 = (Vaihe) child;
                addCategory(child1, prev);
                prev = child1;
                if (firstCategoryId == null) {
                    firstCategoryId = child.getId();
                }
            }
        }
    }

    @JsonIgnore
    public Navigation getNavigation() {
        return navigation;
    }

    @JsonIgnore
    public Vaihe getFirstCategory() {
        return getCategory(firstCategoryId);
    }

    @JsonIgnore
    public Collection<Vaihe> getCategories() {
        return categories.values();
    }

    public Vaihe getVaiheByTeemaId(String teemaId) {
        for (Vaihe vaihe : categories.values()) {
            if (!vaihe.isPreview()) {
                for (Element e : vaihe.getChildren()) {
                    if (e.getId().equals(teemaId)) {
                        return vaihe;
                    }
                }
            }
        }
        return null;
    }
}
