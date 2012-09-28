package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.validation.HakemusState;

import java.util.Map;

/**
 * @author jukka
 * @version 9/26/122:43 PM}
 * @since 1.1
 */
public interface HakemusService {
    Hakemus getHakemus(HakemusId hakemusId);

    HakemusState save(HakemusId hakemusId, Map<String, String> values);
}
