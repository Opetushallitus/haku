package fi.vm.sade.oppija.haku.event;

import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jukka
 * @version 9/28/121:42 PM}
 * @since 1.1
 */
@Service
public class EventHandler {

    List<Event> beforeValidate = new ArrayList<Event>();
    List<Event> validationEvent = new ArrayList<Event>();
    List<Event> postValidate = new ArrayList<Event>();

    public EventHandler() {
    }

    public void processEvents(HakemusState hakemusState) {
        for (Event event : beforeValidate) {
            event.process(hakemusState);
        }
        if (hakemusState.mustValidate()) {
            for (Event event : validationEvent) {
                event.process(hakemusState);
            }
        }
        if (!hakemusState.isValid()) {
            for (Event event : postValidate) {
                event.process(hakemusState);
            }
        }
    }


    public void addBeforeValidationEvent(Event event) {
        this.beforeValidate.add(event);
    }

    public void addValidationEvent(Event event) {
        this.validationEvent.add(event);
    }

    public void addPostValidateEvent(Event event) {
        this.validationEvent.add(event);
    }
}
