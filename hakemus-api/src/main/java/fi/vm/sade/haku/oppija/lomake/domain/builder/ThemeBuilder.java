package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;

public class ThemeBuilder extends TitledBuilder {
    boolean preview;
    boolean configurable;

    public ThemeBuilder(final String id) {
        super(id);
    }

    public ThemeBuilder configurable() {
        this.configurable = true;
        return this;
    }

    public ThemeBuilder previewable() {
        this.preview = true;
        return this;
    }

    Element buildImpl() {
        Theme theme = new Theme(id, i18nText, preview);
        theme.setConfigurable(configurable);
        return theme;
    }

    public static ThemeBuilder Theme(final String id) {
        return new ThemeBuilder(id);
    }
}
