package fi.vm.sade.oppija.lomakkeenhallinta.predicate;

import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IdsTest {

    @Test
    public void testApplyTrue() throws Exception {
        Ids<SubjectRow> nativeLanguages = new Ids<SubjectRow>("AI");
        assertTrue(nativeLanguages.apply(getSubjectRow("AI")));
    }

    @Test
    public void testApplyTrueMultiple() throws Exception {
        Ids<SubjectRow> nativeLanguages = new Ids<SubjectRow>("B1", "AI");
        assertTrue(nativeLanguages.apply(getSubjectRow("AI")));
    }

    @Test
    public void testApplyFalse() throws Exception {
        Ids<SubjectRow> nativeLanguages = new Ids<SubjectRow>("AI");
        assertFalse(nativeLanguages.apply(getSubjectRow("A")));
    }

    @Test
    public void testApplyFalseMultiple() throws Exception {
        Ids<SubjectRow> nativeLanguages = new Ids<SubjectRow>("AI", "B1");
        assertFalse(nativeLanguages.apply(getSubjectRow("A")));
    }

    private SubjectRow getSubjectRow(final String id) {
        return new SubjectRow(id, ElementUtil.createI18NAsIs("sdf"), true, true, false, false);
    }
}
