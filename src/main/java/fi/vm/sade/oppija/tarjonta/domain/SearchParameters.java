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

import java.util.Map;
import java.util.Set;

public class SearchParameters {

    private final String text;
    private final SortParameters sortParameters;
    private final PagingParameters pagingParameters;
    private final Set<String> fields;
    private final Map<String, Map<String, String>> filters;

    public SearchParameters(Map<String, Map<String, String>> filters) {
        this(null, null, null, null, filters);
    }

    public SearchParameters(final String text, final Set<String> fields, final SortParameters sortParameters, final PagingParameters pagingParameters, final Map<String, Map<String, String>> filters) {
        this.text = text;
        this.fields = fields;
        this.pagingParameters = pagingParameters;
        this.sortParameters = sortParameters;
        this.filters = filters;
    }

    public SortParameters getSortParameters() {
        return sortParameters;
    }

    public PagingParameters getPagingParameters() {
        return pagingParameters;
    }

    public Set<String> getFields() {
        return fields;
    }

    public Map<String, Map<String, String>> getFilters() {
        return filters;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "SearchParameters{" +
                "sortParameters=" + sortParameters +
                ", pagingParameters=" + pagingParameters +
                ", fields=" + fields +
                ", filters=" + filters +
                '}';
    }
}
