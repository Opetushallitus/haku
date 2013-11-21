package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.predicate;

import com.google.common.base.Predicate;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SubjectRow;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class SubjectRowPredicateTest {

    Predicate predicate;

    @Test
    public void testApplyTrue() throws Exception {
        assertTrue(predicate.apply(getSubjectRow(true)));
    }

    @Test
    public void testApplyToFalse() throws Exception {
        assertFalse(predicate.apply(getSubjectRow(false)));
    }

    abstract SubjectRow getSubjectRow(boolean b);
}
