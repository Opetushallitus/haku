package fi.vm.sade.haku.oppija.hakemus.aspect;

import fi.vm.sade.haku.oppija.hakemus.domain.PreferenceEligibility;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.*;

public class ApplicationDiffUtilTest {

    private List<PreferenceEligibility> eligibilities;

    @Before
    public void setUp() {
        eligibilities = new LinkedList<>();
        eligibilities.add(new PreferenceEligibility("1.2.246.562.5.14273398983", PreferenceEligibility.Status.NOT_CHECKED, PreferenceEligibility.Source.UNKNOWN, null, null));
        eligibilities.add(new PreferenceEligibility("1.2.246.562.5.41197971199", PreferenceEligibility.Status.ELIGIBLE, PreferenceEligibility.Source.UNKNOWN, null, null));
    }

    @Test
    public void testSameEligibilitiesShouldNotReturnChanges() {
        List<PreferenceEligibility> eligibilities2 = new LinkedList<>();
        eligibilities2.add(new PreferenceEligibility("1.2.246.562.5.14273398983", PreferenceEligibility.Status.NOT_CHECKED, PreferenceEligibility.Source.UNKNOWN, null, null));
        eligibilities2.add(new PreferenceEligibility("1.2.246.562.5.41197971199", PreferenceEligibility.Status.ELIGIBLE, PreferenceEligibility.Source.UNKNOWN, null, null));

        final List<Map<String, String>> changes = ApplicationDiffUtil.oldAndNewEligibilitiesToListOfChanges(eligibilities, eligibilities2);
        assertEquals(changes.size(), 0);
    }

    @Test
    public void testAddedEligibilityShouldAddNewChanged() {
        List<PreferenceEligibility> eligibilities2 = new LinkedList<>(eligibilities);
        eligibilities2.add(new PreferenceEligibility("1.2.246.562.5.14273398984", PreferenceEligibility.Status.NOT_CHECKED, PreferenceEligibility.Source.UNKNOWN, null, null));
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
        eligibilities2.add(new PreferenceEligibility("1.2.246.562.5.41197971199", PreferenceEligibility.Status.NOT_CHECKED, PreferenceEligibility.Source.UNKNOWN, null, null));
        final List<Map<String, String>> changes = ApplicationDiffUtil.oldAndNewEligibilitiesToListOfChanges(eligibilities, eligibilities2);
        assertEquals(changes.size(), 1);
        assertEquals(changes.get(0).get("field"), "eligibility_1_2_246_562_5_41197971199");
        assertEquals(changes.get(0).get("old value"), "ELIGIBLE:UNKNOWN:null");
        assertEquals(changes.get(0).get("new value"), "NOT_CHECKED:UNKNOWN:null");
    }

    @Test
    public void testSameKeyAndValueShouldNotReturnChanges() {
        Map<String, String> oldInfo = new HashMap<>();
        oldInfo.put("info1", "value");
        Map<String, String> newInfo = new HashMap<>();
        newInfo.put("info1", "value");
        List<Map<String, String>> changes = ApplicationDiffUtil.mapsToChanges(oldInfo, newInfo);
        assertEquals(0, changes.size());
    }

    @Test
    public void testNewKeyShouldReturnChanges() {
        Map<String, String> oldInfo = new HashMap<>();
        oldInfo.put("info1", "value");
        Map<String, String> newInfo = new HashMap<>();
        newInfo.put("info1", "value");
        newInfo.put("info2", "value");
        List<Map<String, String>> changes = ApplicationDiffUtil.mapsToChanges(oldInfo, newInfo);
        assertEquals(1, changes.size());
        assertEquals("info2", changes.get(0).get(ApplicationDiffUtil.FIELD));
        assertEquals("value", changes.get(0).get(ApplicationDiffUtil.NEW_VALUE));
    }

    @Test
    public void testRemovedKeyShouldReturnChanges() {
        Map<String, String> oldInfo = new HashMap<>();
        oldInfo.put("info1", "value");
        Map<String, String> newInfo = new HashMap<>();
        List<Map<String, String>> changes = ApplicationDiffUtil.mapsToChanges(oldInfo, newInfo);
        assertEquals(1, changes.size());
        assertEquals("info1", changes.get(0).get(ApplicationDiffUtil.FIELD));
        assertEquals("value", changes.get(0).get(ApplicationDiffUtil.OLD_VALUE));
    }

    @Test
    public void testChangedValueShouldReturnChanges() {
        Map<String, String> oldInfo = new HashMap<>();
        oldInfo.put("info1", "value");
        Map<String, String> newInfo = new HashMap<>();
        newInfo.put("info1", "other value");
        List<Map<String, String>> changes = ApplicationDiffUtil.mapsToChanges(oldInfo, newInfo);
        assertEquals(1, changes.size());
        assertEquals("info1", changes.get(0).get(ApplicationDiffUtil.FIELD));
        assertEquals("value", changes.get(0).get(ApplicationDiffUtil.OLD_VALUE));
        assertEquals("other value", changes.get(0).get(ApplicationDiffUtil.NEW_VALUE));
    }

    @Test
    public void testAddedAndRemovedKeyShouldReturnTwoChanges() {
        Map<String, String> oldInfo = new HashMap<>();
        oldInfo.put("info1", "value");
        Map<String, String> newInfo = new HashMap<>();
        newInfo.put("info2", "value");
        List<Map<String, String>> changes = ApplicationDiffUtil.mapsToChanges(oldInfo, newInfo);
        assertEquals(2, changes.size());
        Set<Map<String, String>> expected = new HashSet<>();
        Map<String, String> expectedAddition = new HashMap<>();
        expectedAddition.put(ApplicationDiffUtil.FIELD, "info1");
        expectedAddition.put(ApplicationDiffUtil.OLD_VALUE, "value");
        Map<String, String> expectedDeletion = new HashMap<>();
        expectedDeletion.put(ApplicationDiffUtil.FIELD, "info2");
        expectedDeletion.put(ApplicationDiffUtil.NEW_VALUE, "value");
        expected.add(expectedAddition);
        expected.add(expectedDeletion);
        assertEquals(expected, new HashSet<>(changes));
    }
}
