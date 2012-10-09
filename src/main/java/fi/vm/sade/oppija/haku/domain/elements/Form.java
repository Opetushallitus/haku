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

    final transient Map<String, Category> categories = new HashMap<String, Category>();

    public Form(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
    }

    public Category getCategory(String categoryId) {
        return categories.get(categoryId);
    }

    private void addCategory(Category category, Category prev) {
        this.categories.put(category.getId(), category);
        category.initChain(prev);
        navigation.addChild(category.asLink());
    }

    public void init() {
        Category prev = null;
        for (Element child : children) {
            if (child instanceof Category) {
                final Category child1 = (Category) child;
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
    public Category getFirstCategory() {
        return getCategory(firstCategoryId);
    }
}
