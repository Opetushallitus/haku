package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.predicate;

import com.google.common.base.Predicate;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SubjectRow;

public class HighSchools implements Predicate<SubjectRow> {

    @Override
    public boolean apply(final SubjectRow subjectRow) {
        return subjectRow.isHighSchool();
    }
}
