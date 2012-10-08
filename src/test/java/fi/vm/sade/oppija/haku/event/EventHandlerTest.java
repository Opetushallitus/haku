package fi.vm.sade.oppija.haku.event;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * @author jukka
 * @version 10/8/1210:10 AM}
 * @since 1.1
 */
public class EventHandlerTest {
    @Test
    public void testProcessEvents() throws Exception {
        final EventHandler eventHandler = createErrorEvent(new EventHandler());
        final HakemusState hakemusState = createEmptyState();
        eventHandler.processEvents(hakemusState);
        assertEquals(hakemusState.getErrorMessages().get("error"), "error");
    }

    private HakemusState createEmptyState() {
        final String id = "id";
        return new HakemusState(new Hakemus(new HakemusId(id, id, id, id), new HashMap<String, String>()));
    }

    private EventHandler createErrorEvent(EventHandler eventHandler1) {
        eventHandler1.addValidationEvent(new Event() {
            @Override
            public void process(HakemusState hakemusState) {
                hakemusState.getErrorMessages().put("error", "error");
            }
        });
        return eventHandler1;
    }

    private EventHandler createBeforeEvent(EventHandler eventHandler1) {
        eventHandler1.addBeforeValidationEvent(new Event() {
            @Override
            public void process(HakemusState hakemusState) {
                hakemusState.addModelObject("before", "before");
            }
        });
        return eventHandler1;
    }

    @Test
    public void testAddValidationEvent() throws Exception {
        final EventHandler eventHandler = new EventHandler();
        createBeforeEvent(eventHandler);
        createErrorEvent(eventHandler);
        final HakemusState emptyState = createEmptyState();
        eventHandler.processEvents(emptyState);

        assertEquals("before", emptyState.getModelObjects().get("before"));
        assertEquals("error", emptyState.getErrorMessages().get("error"));

    }
}
