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

package fi.vm.sade.oppija.tarjonta.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchResult {
    final List<Map<String, Object>> items;
    final Set<String> sortFields;

    public SearchResult(List<Map<String, Object>> items) {
        this.items = items;
        if (items.isEmpty()) {
            sortFields = Collections.<String>emptySet();
        } else {
            this.sortFields = items.get(0).keySet();
        }
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public Set<String> getSortFields() {
        return sortFields;
    }

    public int getSize() {
        return items.size();
    }
}
