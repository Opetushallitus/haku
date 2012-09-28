package fi.vm.sade.oppija.haku.event;

import fi.vm.sade.oppija.haku.validation.HakemusState;

/**
 * @author jukka
 * @version 9/28/122:39 PM}
 * @since 1.1
 */
public abstract class AbstractEvent implements Event {
    @Override
    public abstract void process(HakemusState hakemusState);
}
