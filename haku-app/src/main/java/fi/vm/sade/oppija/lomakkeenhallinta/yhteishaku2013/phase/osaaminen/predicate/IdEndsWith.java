package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen.predicate;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import fi.vm.sade.oppija.lomake.domain.elements.Element;

public class IdEndsWith implements Predicate<Element> {
    private final String idSuffix;

    public IdEndsWith(final String idSuffix) {
        Preconditions.checkNotNull(idSuffix);
        this.idSuffix = idSuffix;
    }

    @Override
    public boolean apply(Element element) {
        return element.getId().endsWith(idSuffix);
    }
}
