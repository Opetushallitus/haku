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

package fi.vm.sade.haku.oppija.hakemus.aspect;

import fi.vm.sade.haku.oppija.lomake.service.mock.UserHolderMock;
import fi.vm.sade.log.client.Logger;
import fi.vm.sade.log.model.Tapahtuma;
import org.junit.Test;

public class LoggerAspectTest {

    public static final LoggerAspect LOGGER_ASPECT = new LoggerAspect(new Logger() {
        @Override
        public void log(Tapahtuma tapahtuma) {

        }
    }, new UserHolderMock("test"));

    @Test
    public void testlogSubmitApplication() throws Exception {
        LOGGER_ASPECT.logSubmitApplication("aid", "oid");
    }

    @Test
    public void testLogSavePhaseNulls() throws Exception {
        LOGGER_ASPECT.logSubmitApplication(null, null);
    }
}
