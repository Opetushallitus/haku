package fi.vm.sade.haku.oppija.hakemus;

import java.util.Map;

public class KoulutustaustaEntry implements Map.Entry<String, String> {

    private final String key;

    private final String value;

    public KoulutustaustaEntry(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String setValue(String value) {
        throw new IllegalArgumentException("value is immutable");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KoulutustaustaEntry that = (KoulutustaustaEntry) o;

        if (!key.equals(that.key)) return false;
        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

}
