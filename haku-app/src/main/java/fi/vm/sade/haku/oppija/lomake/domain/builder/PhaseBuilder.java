package fi.vm.sade.haku.oppija.lomake.domain.builder;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;

import java.util.List;

public class PhaseBuilder extends ElementBuilder {
    private List<String> editAllowedByRoles = ImmutableList.of("APP_HAKEMUS_READ_UPDATE", "APP_HAKEMUS_CRUD", "APP_HAKEMUS_OPO");
    boolean preview;

    public PhaseBuilder(String id) {
        super(id);
        key = "form." + id + ".otsikko";
    }

    public PhaseBuilder preview() {
        this.preview = true;
        return this;
    }

    @Override
    Element buildImpl() {
        return new Phase(id, i18nText, preview, editAllowedByRoles);
    }

    public static PhaseBuilder Phase(final String id) {
        return new PhaseBuilder(id);
    }
}
