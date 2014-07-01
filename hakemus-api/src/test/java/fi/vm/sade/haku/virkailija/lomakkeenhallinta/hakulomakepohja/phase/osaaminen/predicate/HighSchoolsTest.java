package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.predicate;

import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
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
