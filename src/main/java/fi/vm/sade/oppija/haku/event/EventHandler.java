/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

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

    public HakemusState processEvents(HakemusState hakemusState) {
        for (Event event : beforeValidate) {
            event.process(hakemusState);
        }
        if (hakemusState.mustValidate()) {
            for (Event event : validationEvent) {
                event.process(hakemusState);
            }
        }
        if (hakemusState.isValid()) {
            for (Event event : postValidate) {
                event.process(hakemusState);
            }
        }
        return hakemusState;
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
