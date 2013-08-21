package fi.vm.sade.oppija.common.diff;

import com.google.common.collect.MapDifference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnswersDifference {

    List<Difference> differences = new ArrayList<Difference>();

    public AnswersDifference(MapDifference<String, String> difference) {

        Map<String, String> entriesOnlyOnLeft = difference.entriesOnlyOnLeft();
        for (Map.Entry<String, String> onLeft : entriesOnlyOnLeft.entrySet()) {
            differences.add(new Difference(onLeft.getKey(), onLeft.getValue(), null));
        }
        Map<String, MapDifference.ValueDifference<String>> entriesDiffering = difference.entriesDiffering();
        for (Map.Entry<String, MapDifference.ValueDifference<String>> entryDiffering : entriesDiffering.entrySet()) {
            differences.add(new Difference(entryDiffering));
        }
        Map<String, String> entriesOnlyOnRight = difference.entriesOnlyOnRight();
        for (Map.Entry<String, String> onRight : entriesOnlyOnRight.entrySet()) {
            differences.add(new Difference(onRight.getKey(), null, onRight.getValue()));
        }
    }

    public List<Difference> getDifferences() {
        return differences;
    }
}
