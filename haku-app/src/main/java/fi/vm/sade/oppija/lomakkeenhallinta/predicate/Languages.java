package fi.vm.sade.oppija.lomakkeenhallinta.predicate;

import com.google.common.base.Predicate;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SubjectRow;

public class Languages implements Predicate<SubjectRow> {

    @Override
    public boolean apply(final SubjectRow subjectRow) {
        return subjectRow.isLanguage();
    }
}
