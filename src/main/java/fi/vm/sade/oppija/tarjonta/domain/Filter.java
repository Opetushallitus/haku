package fi.vm.sade.oppija.tarjonta.domain;

import java.util.Collections;
import java.util.List;

public class Filter {

    private final String name;
    private final List<FilterValue> filterValues;


    public Filter(final String name, final List<FilterValue> filterValues) {
        this.name = name;
        this.filterValues = Collections.unmodifiableList(filterValues);
    }

    public String getName() {
        return name;
    }

    public List<FilterValue> getFilterValues() {
        return filterValues;
    }
}
