package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;

/**
 * @author jukka
 * @version 9/26/122:43 PM}
 * @since 1.1
 */
public interface HakemusService {
    void save(Hakemus hakemus);

    Hakemus getHakemus(HakemusId hakemusId);
}
