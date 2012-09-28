package fi.vm.sade.oppija.haku.event;

import fi.vm.sade.oppija.haku.validation.HakemusState;

/**
 * @author jukka
 * @version 9/28/121:43 PM}
 * @since 1.1
 */
public interface Event {
    void process(HakemusState hakemusState);
}
