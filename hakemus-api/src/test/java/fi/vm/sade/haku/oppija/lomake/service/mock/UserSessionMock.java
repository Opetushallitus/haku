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
package fi.vm.sade.haku.oppija.lomake.service.mock;

import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.service.UserSession;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

public class UserSessionMock extends UserSession {

    private static final long serialVersionUID = 1956696173293019715L;
    private final User user;


    public UserSessionMock(String username) {
        this.user = new User(username);
    }

    @Override
    public User getUser() {
        return this.user;
    }

}
