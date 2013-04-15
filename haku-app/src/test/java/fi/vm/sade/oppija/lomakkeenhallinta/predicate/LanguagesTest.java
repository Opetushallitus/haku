package fi.vm.sade.oppija.lomakkeenhallinta.predicate;

import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import org.junit.Before;

public class LanguagesTest extends SubjectRowPredicateTest {

    @Before
    public void setUp() throws Exception {
        predicate = new Languages();
    }

    SubjectRow getSubjectRow(final boolean language) {
        return new SubjectRow("id", ElementUtil.createI18NForm("sdf"), true, false, true, language);
    }
}
