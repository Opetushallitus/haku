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

package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakuLomakeId;
import fi.vm.sade.oppija.haku.domain.VaiheenVastaukset;
import fi.vm.sade.oppija.haku.service.UserHolder;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDataStorage {
    final ApplicationDAO sessionDataHolder;
    final ApplicationDAO applicationDAO;
    final UserHolder userHolder;

    @Autowired
    public UserDataStorage(@Qualifier("sessionDataHolder") ApplicationDAO sessionDataHolder, @Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO, UserHolder userHolder) {
        this.sessionDataHolder = sessionDataHolder;
        this.applicationDAO = applicationDAO;
        this.userHolder = userHolder;
    }

    public HakemusState tallenna(HakemusState state) {
        if (state.isFinalStage()) {
            return applicationDAO.laitaVireille(state);
        }
        return applicationDAO.tallennaVaihe(state);
    }

    private ApplicationDAO selectDao() {
        ApplicationDAO dao = sessionDataHolder;
        if (userHolder.isUserKnown()) {
            dao = applicationDAO;
        }
        return dao;
    }

    public Hakemus find(HakuLomakeId hakuLomakeId) {
        return applicationDAO.find(hakuLomakeId, userHolder.getUser());
    }

    public HakemusState initHakemusState(VaiheenVastaukset vaihe) {
        return new HakemusState(new Hakemus(userHolder.getUser(), vaihe), vaihe.getVaiheId());

    }

    public List<Hakemus> findAll() {
        return applicationDAO.findAll(userHolder.getUser());
    }

}
