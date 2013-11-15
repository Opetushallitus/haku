package fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.predicate;

import com.google.common.base.Predicate;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;

public class ComprehensiveSchools implements Predicate<SubjectRow> {

    @Override
    public boolean apply(final SubjectRow subjectRow) {
        return subjectRow.isComprehensiveSchool();
    }
}
