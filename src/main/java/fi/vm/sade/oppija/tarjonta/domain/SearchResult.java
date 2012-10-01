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
}
