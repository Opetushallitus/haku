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
