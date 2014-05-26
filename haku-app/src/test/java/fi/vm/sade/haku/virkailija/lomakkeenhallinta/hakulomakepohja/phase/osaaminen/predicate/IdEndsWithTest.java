package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.predicate;

import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IdEndsWithTest {

    public static final String SUFFIX = "suffix";
    public static final String ID = "id" + SUFFIX;
    private IdEndsWith idEndsWith = new IdEndsWith(SUFFIX);

    @Test
    public void testApplyTrue() throws Exception {
        boolean actual = idEndsWith.apply(new TextQuestionBuilder(ID).i18nText(ElementUtil.createI18NAsIs("")).build());
        assertTrue(actual);
    }

    @Test
    public void testApplyFalse() throws Exception {
        boolean actual = idEndsWith.apply(new TextQuestionBuilder(ID + 'T').i18nText(ElementUtil.createI18NAsIs("")).build());
        assertFalse(actual);
    }

}
