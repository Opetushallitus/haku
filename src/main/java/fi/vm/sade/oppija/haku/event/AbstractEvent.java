package fi.vm.sade.oppija.haku.event;

import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author jukka
 * @version 9/28/122:39 PM}
 * @since 1.1
 */
public abstract class AbstractEvent implements Event {
    private EventHandler eventHandler;

    @Autowired
    public AbstractEvent(EventHandler eventHandler) {
        eventHandler.addEvent(this);
    }

    @Override
    public abstract void process(HakemusState hakemusState);
}
