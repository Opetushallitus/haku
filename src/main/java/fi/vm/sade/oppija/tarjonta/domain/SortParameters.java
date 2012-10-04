package fi.vm.sade.oppija.tarjonta.domain;

public class SortParameters {
    private final String sortOrder;
    private final String sortField;

    public SortParameters(final String sortOrder, final String sortField) {
        this.sortOrder = "asc".equalsIgnoreCase(sortOrder) ? "acs" : "decs";
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public String getSortField() {
        return sortField;
    }

    @Override
    public String toString() {
        return "SortParameters{" +
                "sortOrder='" + sortOrder + '\'' +
                ", sortField='" + sortField + '\'' +
                '}';
    }
}
