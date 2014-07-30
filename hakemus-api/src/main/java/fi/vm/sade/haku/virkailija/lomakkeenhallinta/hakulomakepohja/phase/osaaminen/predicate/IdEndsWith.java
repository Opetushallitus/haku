package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.predicate;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;

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
