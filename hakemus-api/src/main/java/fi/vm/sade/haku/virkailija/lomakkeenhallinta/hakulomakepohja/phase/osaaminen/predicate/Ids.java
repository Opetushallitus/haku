package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.predicate;

import com.google.common.base.Predicate;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ids<T extends Element> implements Predicate<T> {

    private final List<String> ids = new ArrayList<String>();

    public Ids(String... ids) {
        this.ids.addAll(Arrays.asList(ids));
    }

    @Override
    public boolean apply(T input) {
        return this.ids.contains(input.getId());
    }
}
