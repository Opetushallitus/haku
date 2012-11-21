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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jukka
 * @version 9/28/121:42 PM}
 * @since 1.1
 */
@Service
public class ValidationHandler {

    private final PreValidationEvent preValidationEvent;
    private final ValidationEvent validationEvent;
    private final NavigationEvent navigationEvent;

    @Autowired
    public ValidationHandler(PreValidationEvent preValidationEvent, ValidationEvent validationEvent, NavigationEvent navigationEvent) {
        this.preValidationEvent = preValidationEvent;
        this.validationEvent = validationEvent;
        this.navigationEvent = navigationEvent;
    }

    public HakemusState processEvents(HakemusState hakemusState) {
        preValidationEvent.process(hakemusState);
        validationEvent.process(hakemusState);
        navigationEvent.process(hakemusState);
        return hakemusState;
    }

}
