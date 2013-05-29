package fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.osaaminen;

import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NForm;

public class OsaaminenPhase {

    private Theme arvosanat;
    private Theme kielitaito;
    private final Phase osaaminen;

    public OsaaminenPhase(final KoodistoService koodistoService) {
        osaaminen = new Phase("osaaminen", createI18NForm("form.osaaminen.otsikko"), false);
        this.arvosanat = ArvosanatTheme.createArvosanatTheme(koodistoService);
        this.kielitaito = KielitaitokysymyksetTheme.createKielitaitokysymyksetTheme();
        osaaminen.addChild(arvosanat);
        osaaminen.addChild(kielitaito);
    }

    public Theme getArvosanat() {
        return arvosanat;
    }

    public Theme getKielitaito() {
        return kielitaito;
    }

    public Phase getOsaaminen() {
        return osaaminen;
    }
}
