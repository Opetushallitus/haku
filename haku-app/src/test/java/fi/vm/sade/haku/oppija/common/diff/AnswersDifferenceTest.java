package fi.vm.sade.haku.oppija.common.diff;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class AnswersDifferenceTest {

    public static final String OLD_VALUE = "Saku";
    public static final String NEW_VALUE = "Tero";
    public static final String KEY = "etunimi";

    @Test
    public void testGetDifferencesSame() {
        ImmutableMap<String, String> answers = ImmutableMap.of(KEY, OLD_VALUE);
        AnswersDifference answersDifference = new AnswersDifference(Maps.difference(answers, answers));
        assertTrue(answersDifference.getDifferences().isEmpty());
    }

    @Test
    public void testGetDifferences() {
        AnswersDifference answersDifference = new AnswersDifference(Maps.difference(ImmutableMap.of(KEY, OLD_VALUE),
                ImmutableMap.of(KEY, NEW_VALUE)));
        assertTrue(answersDifference.getDifferences().size() == 1);
        assertEquals("Old and new values are wrong", NEW_VALUE, answersDifference.getDifferences().get(0).getNewValue());
        assertEquals("Old and new values are wrong", OLD_VALUE, answersDifference.getDifferences().get(0).getOldValue());
    }
}
