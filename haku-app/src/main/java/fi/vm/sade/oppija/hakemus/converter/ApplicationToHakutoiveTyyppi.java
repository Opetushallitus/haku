package fi.vm.sade.oppija.hakemus.converter;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.service.hakemus.schema.HakutoiveTyyppi;

/**
 * @author Mikko Majapuro
 */
public class ApplicationToHakutoiveTyyppi extends AbstractApplicationConverter<HakutoiveTyyppi> {

    @Override
    public HakutoiveTyyppi convert(Application application) {
        HakutoiveTyyppi hakutoiveTyyppi = new HakutoiveTyyppi();
        hakutoiveTyyppi.setHakemusOid(application.getOid());
        hakutoiveTyyppi.getHakutoive().addAll(getPreferences(application.getVastauksetMerged()));
        return hakutoiveTyyppi;
    }
}
