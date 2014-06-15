package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;

public class ThemeBuilder extends ElementBuilder {
    boolean preview;

    public ThemeBuilder(final String id) {
        super(id);
    }

    public ThemeBuilder previewable() {
        this.preview = true;
        return this;
    }

    Element buildImpl() {
        Theme theme = new Theme(id, i18nText, preview);
        return theme;
    }

    public static ThemeBuilder Theme(final String id) {
        return new ThemeBuilder(id);
    }
}
