package fi.vm.sade.haku.oppija.lomake.domain.builder;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.hakemus.service.Role;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.exception.ConfigurationException;

import java.util.List;

public class PhaseBuilder extends TitledBuilder {
    private List<String> editAllowedByRoles = null;
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
        if (editAllowedByRoles == null || editAllowedByRoles.isEmpty())
            throw new ConfigurationException("Missing configuration for editAllowedByRoles");
        return new Phase(id, i18nText, preview, editAllowedByRoles);
    }

    public static PhaseBuilder Phase(final String id) {
        return new PhaseBuilder(id);
    }

    public PhaseBuilder setEditAllowedByRoles(List<String> editAllowedByRoles){
        this.editAllowedByRoles=editAllowedByRoles;
        return this;
    }

    public PhaseBuilder setEditAllowedByRoles(String... editAllowedByRoles){
        return this.setEditAllowedByRoles(ImmutableList.copyOf(editAllowedByRoles));
    }

    public PhaseBuilder setEditAllowedByRoles(Role... editAllowedByRoles){
        final ImmutableList.Builder<String> list = new ImmutableList.Builder<>();
        for(Role role : editAllowedByRoles) {
            list.add(role.casName);
        }
        return this.setEditAllowedByRoles(list.build());
    }
}
