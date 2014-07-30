package fi.vm.sade.haku.oppija.common.diff;

import com.google.common.collect.MapDifference;

import java.util.Map;

public class Difference {
    private final String key;
    private final String oldValue;
    private final String newValue;

    public Difference(String key, String oldValue, String newValue) {
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Difference(Map.Entry<String, MapDifference.ValueDifference<String>> entryDiffering) {
        this.key = entryDiffering.getKey();
        this.oldValue = entryDiffering.getValue().leftValue();
        this.newValue = entryDiffering.getValue().rightValue();
    }

    public String getKey() {
        return key;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }
}
