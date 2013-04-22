package fi.vm.sade.oppija.lomakkeenhallinta.predicate;

import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import org.junit.Before;

public class HighSchoolsTest extends SubjectRowPredicateTest {

    @Before
    public void setUp() throws Exception {
        this.predicate = new HighSchools();
    }

    @Override
    SubjectRow getSubjectRow(final boolean highSchool) {
        return new SubjectRow("id", ElementUtil.createI18NAsIs("sdf"), true, highSchool, true, false);
    }
}
