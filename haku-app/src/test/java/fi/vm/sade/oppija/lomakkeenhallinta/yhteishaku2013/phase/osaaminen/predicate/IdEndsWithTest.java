package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.predicate;

import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IdEndsWithTest {

    public static final String SUFFIX = "suffix";
    public static final String ID = "id" + SUFFIX;
    private IdEndsWith idEndsWith = new IdEndsWith(SUFFIX);

    @Test
    public void testApplyTrue() throws Exception {
        boolean actual = idEndsWith.apply(new TextQuestion(ID, ElementUtil.createI18NAsIs("")));
        assertTrue(actual);
    }

    @Test
    public void testApplyFalse() throws Exception {
        boolean actual = idEndsWith.apply(new TextQuestion(ID + 'T', ElementUtil.createI18NAsIs("")));
        assertFalse(actual);
    }

}
