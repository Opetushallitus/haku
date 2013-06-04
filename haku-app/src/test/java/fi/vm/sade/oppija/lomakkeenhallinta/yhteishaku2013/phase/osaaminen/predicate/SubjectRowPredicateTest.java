package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.predicate;

import com.google.common.base.Predicate;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;
import org.junit.Test;

import static com.mongodb.util.MyAsserts.assertTrue;
import static org.junit.Assert.assertFalse;

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
