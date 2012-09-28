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

    List<Event> list = new ArrayList<Event>();

    public void processEvents(HakemusState hakemusState) {
        for (Event event : list) {
            event.process(hakemusState);
        }
    }


    public void addEvent(Event event) {
        this.list.add(event);
    }
}
