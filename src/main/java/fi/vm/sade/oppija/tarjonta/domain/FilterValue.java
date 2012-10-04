package fi.vm.sade.oppija.tarjonta.domain;

public class FilterValue {
    final String name;
    final String label;

    public FilterValue(final String name, final String label) {
        this.name = name;
        this.label = label;
    }

    public FilterValue(final String name) {
        this.name = name;
        this.label = name;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }
}
