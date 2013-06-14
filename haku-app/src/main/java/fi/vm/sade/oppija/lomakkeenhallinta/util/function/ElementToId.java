package fi.vm.sade.oppija.lomakkeenhallinta.util.function;

import com.google.common.base.Function;
import fi.vm.sade.oppija.lomake.domain.elements.Element;

public class ElementToId implements Function<Element, String> {
    @Override
    public String apply(Element element) {
        return element.getId();

    }
}
