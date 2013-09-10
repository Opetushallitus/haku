package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.esikatselu;

import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;

import java.util.List;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NForm;
import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.findElementsByTypeAsList;

public class EsikatseluPhase {

    private EsikatseluPhase() {
    }

    public static Phase create(final Form form) {
        Phase esikatselu = new Phase("esikatselu", createI18NForm("form.esikatselu.otsikko"), true);

        List<Theme> themes = findElementsByTypeAsList(form, Theme.class);

        for (Theme theme : themes) {
            if (theme.isPreviewable()) {
                esikatselu.addChild(theme);
            }
        }
        return esikatselu;
    }
}
