package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;

public class ThemeBuilder extends ElementBuilder {
    boolean preview;

    public ThemeBuilder(final String id) {
        super(id);
    }

    public ThemeBuilder previewable() {
        this.preview = true;
        return this;
    }

    @Override
    public Element buildImpl(final FormParameters formParameters) {
        Theme theme = new Theme(id, i18nText, preview);
        theme.setInline(this.inline);
        return theme;
    }

    public Element buildImpl() {
        Theme theme = new Theme(id, i18nText, preview);
        theme.setInline(this.inline);
        return theme;
    }

    public static ThemeBuilder Theme(final String id) {
        return new ThemeBuilder(id);
    }
}
