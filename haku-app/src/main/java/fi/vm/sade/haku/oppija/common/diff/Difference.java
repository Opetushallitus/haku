package fi.vm.sade.haku.oppija.common.diff;

import com.google.common.collect.MapDifference;

import java.util.Map;

public class Difference {
    private final String key;
    private final String newValue;
    private final String oldValue;

    public Difference(String key, String newValue, String oldValue) {
        this.key = key;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    public Difference(Map.Entry<String, MapDifference.ValueDifference<String>> entryDiffering) {
        this.key = entryDiffering.getKey();
        this.oldValue = entryDiffering.getValue().leftValue();
        this.newValue = entryDiffering.getValue().rightValue();
    }

    public String getKey() {
        return key;
    }

    public String getNewValue() {
        return newValue;
    }

    public String getOldValue() {
        return oldValue;
    }
}
