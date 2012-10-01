package fi.vm.sade.oppija.tarjonta.domain;

public class SearchParameters {

    private final String searchField;
    private final String term;
    private final String sortOrder;
    private final String sortField;
    private final Integer start;
    private final Integer rows;
    private final String[] fields;

    public SearchParameters(final String searchField, final String term, final String sortOrder, final String sortField, final Integer start, final Integer rows, final String... fields) {
        assert term != null;
        this.searchField = searchField;
        this.term = term;
        this.sortOrder = sortOrder;
        this.sortField = sortField;
        this.start = start;
        this.rows = rows;
        this.fields = fields;
    }

    public String getSearchField() {
        return searchField;
    }

    public String getTerm() {
        return term;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public String getSortField() {
        return sortField;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getRows() {
        return rows;
    }

    public String[] getFields() {
        return fields;
    }
}
