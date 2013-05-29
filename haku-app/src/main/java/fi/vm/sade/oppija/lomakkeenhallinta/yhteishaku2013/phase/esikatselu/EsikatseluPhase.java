package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.esikatselu;

import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;

import java.util.List;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NForm;

public class EsikatseluPhase {

    private final Phase esikatselu;

    public EsikatseluPhase(final Form form) {
        esikatselu = new Phase("esikatselu", createI18NForm("form.esikatselu.otsikko"), true);

        List<Element> phases = form.getChildren();
        for (Element phase : phases) {
            List<Element> children = phase.getChildren();
            for (Element child : children) {
                if (child instanceof Theme) {
                    if (((Theme) child).isPreviewable()) {
                        esikatselu.addChild(child);
                    }
                }
            }
        }
    }

    public Phase getEsikatselu() {
        return esikatselu;
    }

}
