package fi.vm.sade.haku.oppija.hakemus.aspect;

import fi.vm.sade.haku.oppija.hakemus.domain.PreferenceEligibility;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ApplicationDiffUtilTest {

    private List<PreferenceEligibility> eligibilities;

    @Before
    public void setUp() {
        eligibilities = new LinkedList<>();
        eligibilities.add(new PreferenceEligibility("1.2.246.562.5.14273398983", PreferenceEligibility.Status.NOT_CHECKED, PreferenceEligibility.Source.UNKNOWN, null));
        eligibilities.add(new PreferenceEligibility("1.2.246.562.5.41197971199", PreferenceEligibility.Status.ELIGIBLE, PreferenceEligibility.Source.UNKNOWN, null));
    }

    @Test
    public void testSameEligibilitiesShouldNotReturnChanges() {
        List<PreferenceEligibility> eligibilities2 = new LinkedList<>();
        eligibilities2.add(new PreferenceEligibility("1.2.246.562.5.14273398983", PreferenceEligibility.Status.NOT_CHECKED, PreferenceEligibility.Source.UNKNOWN, null));
        eligibilities2.add(new PreferenceEligibility("1.2.246.562.5.41197971199", PreferenceEligibility.Status.ELIGIBLE, PreferenceEligibility.Source.UNKNOWN, null));

        final List<Map<String, String>> changes = ApplicationDiffUtil.oldAndNewEligibilitiesToListOfChanges(eligibilities, eligibilities2);
        assertEquals(changes.size(), 0);
    }

    @Test
    public void testAddedEligibilityShouldAddNewChanged() {
        List<PreferenceEligibility> eligibilities2 = new LinkedList<>(eligibilities);
        eligibilities2.add(new PreferenceEligibility("1.2.246.562.5.14273398984", PreferenceEligibility.Status.NOT_CHECKED, PreferenceEligibility.Source.UNKNOWN, null));
        final List<Map<String, String>> changes = ApplicationDiffUtil.oldAndNewEligibilitiesToListOfChanges(eligibilities, eligibilities2);
        assertEquals(changes.size(), 1);
        assertEquals(changes.get(0).get("field"), "eligibility_1_2_246_562_5_14273398984");
        assertEquals(changes.get(0).get("new value"), "NOT_CHECKED:UNKNOWN:null");
    }

    @Test
    public void testRemovedEligibilityShouldAddNewChanged() {
        List<PreferenceEligibility> eligibilities2 = new LinkedList<>(eligibilities);
        eligibilities2.remove(1);
        final List<Map<String, String>> changes = ApplicationDiffUtil.oldAndNewEligibilitiesToListOfChanges(eligibilities, eligibilities2);
        assertEquals(changes.size(), 1);
        assertEquals(changes.get(0).get("field"), "eligibility_1_2_246_562_5_41197971199");
        assertEquals(changes.get(0).get("old value"), "ELIGIBLE:UNKNOWN:null");
    }

    @Test
    public void testChangedEligibilityShouldAddNewChanged() {
        List<PreferenceEligibility> eligibilities2 = new LinkedList<>(eligibilities);
        eligibilities2.remove(1);
        eligibilities2.add(new PreferenceEligibility("1.2.246.562.5.41197971199", PreferenceEligibility.Status.NOT_CHECKED, PreferenceEligibility.Source.UNKNOWN, null));
        final List<Map<String, String>> changes = ApplicationDiffUtil.oldAndNewEligibilitiesToListOfChanges(eligibilities, eligibilities2);
        assertEquals(changes.size(), 1);
        assertEquals(changes.get(0).get("field"), "eligibility_1_2_246_562_5_41197971199");
        assertEquals(changes.get(0).get("old value"), "ELIGIBLE:UNKNOWN:null");
        assertEquals(changes.get(0).get("new value"), "NOT_CHECKED:UNKNOWN:null");
    }
}
