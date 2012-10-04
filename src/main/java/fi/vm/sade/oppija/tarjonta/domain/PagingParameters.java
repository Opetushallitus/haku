package fi.vm.sade.oppija.tarjonta.domain;

public class PagingParameters {
    private final Integer start;
    private final Integer rows;

    public PagingParameters(final Integer start, final Integer rows) {
        this.start = start;
        this.rows = rows;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return "PagingParameters{" +
                "start=" + start +
                ", rows=" + rows +
                '}';
    }
}
