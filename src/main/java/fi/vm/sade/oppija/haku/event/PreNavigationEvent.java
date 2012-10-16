package fi.vm.sade.oppija.haku.event;

import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author jukka
 * @version 10/16/121:22 PM}
 * @since 1.1
 */
@Service
public class PreNavigationEvent extends AbstractEvent {

    private static final String NAV_NEXT = "nav-next";
    private static final String NAV_PREV = "nav-prev";

    @Autowired
    public PreNavigationEvent(EventHandler eventHandler) {
        eventHandler.addBeforeValidationEvent(this);
    }

    @Override
    public void process(HakemusState hakemusState) {
        removeNext(hakemusState);
        removePrev(hakemusState);
    }

    private void removePrev(HakemusState hakemusState) {
        final Map<String, String> values = hakemusState.getHakemus().getValues();
        if (values.containsKey(NAV_PREV)) {
            hakemusState.toggleNavigatePrev();
            values.remove(NAV_PREV);
        }
    }

    private void removeNext(HakemusState hakemusState) {
        final Map<String, String> values = hakemusState.getHakemus().getValues();
        if (values.containsKey(NAV_NEXT)) {
            hakemusState.toggleNavigateNext();
            values.remove(NAV_NEXT);
        }
    }
}
