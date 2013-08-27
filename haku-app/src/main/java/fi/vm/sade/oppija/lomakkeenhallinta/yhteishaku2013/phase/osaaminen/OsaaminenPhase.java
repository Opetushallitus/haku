package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen;

import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NForm;

public class OsaaminenPhase {

    public static Phase create(final KoodistoService koodistoService) {
        Phase osaaminen = new Phase("osaaminen", createI18NForm("form.osaaminen.otsikko"), false);
        osaaminen.addChild(ArvosanatTheme.createArvosanatTheme(koodistoService));
        osaaminen.addChild(KielitaitokysymyksetTheme.createKielitaitokysymyksetTheme());
        return osaaminen;
    }
}
