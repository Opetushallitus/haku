package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.predicate;

import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;

public class ComprehensiveSchoolsTest extends SubjectRowPredicateTest {

    @Before
    public void setUp() throws Exception {
        this.predicate = new ComprehensiveSchools();

    }

    @Override
    SubjectRow getSubjectRow(final boolean comprehensiveSchool) {
        return new SubjectRow("id", ElementUtil.createI18NAsIs("sdf"), true, true, comprehensiveSchool, false);
    }
}
