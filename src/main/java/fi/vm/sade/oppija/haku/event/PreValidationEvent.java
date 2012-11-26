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

import java.util.Map;

/**
 * @author jukka
 * @version 10/16/121:22 PM}
 * @since 1.1
 */
public class PreValidationEvent implements Event {

    private static final String VAIHE_ID = "vaiheId";

    @Override
    public void process(HakemusState hakemusState) {
        removeVaiheId(hakemusState);
    }

    private void removeVaiheId(HakemusState hakemusState) {
        final Map<String, String> values = hakemusState.getHakemus().getVastauksetMerged();
        if (values.containsKey(VAIHE_ID)) {
            values.remove(VAIHE_ID);
        }
    }
}
