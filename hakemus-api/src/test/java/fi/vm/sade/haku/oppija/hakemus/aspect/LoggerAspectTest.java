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

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.service.mock.UserSessionMock;
import fi.vm.sade.haku.oppija.repository.AuditLogRepository;
import fi.vm.sade.log.client.Logger;
import fi.vm.sade.log.model.Tapahtuma;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LoggerAspectTest {

    private static AuditLogRepository auditMock = mock(AuditLogRepository.class);
    public static final LoggerAspect LOGGER_ASPECT = new LoggerAspect(new Logger() {
        @Override
        public void log(Tapahtuma tapahtuma) {

        }
    }, new UserSessionMock("test"), auditMock);

    @Test
    public void testlogSubmitApplication() throws Exception {
        LOGGER_ASPECT.logSubmitApplication("aid", new Application("oid"));
        verify(auditMock).save((Tapahtuma)any());
    }

    @Test
    public void testLogSavePhaseNulls() throws Exception {
        LOGGER_ASPECT.logSubmitApplication(null, null);
    }
}
