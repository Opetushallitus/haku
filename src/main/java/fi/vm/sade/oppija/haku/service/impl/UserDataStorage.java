package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.service.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    public void updateApplication(Hakemus hakemus) {
        selectDao().update(hakemus);
    }

    private ApplicationDAO selectDao() {
        ApplicationDAO dao = sessionDataHolder;
        if (userHolder.isUserKnown()) {
            dao = applicationDAO;
        }
        return dao;
    }

    public Hakemus find(HakemusId hakemusId) {
        return selectDao().find(hakemusId, userHolder.getUser());
    }

    public Hakemus initHakemus(HakemusId hakemusId, Map<String, String> values) {
        return new Hakemus(hakemusId, values, userHolder.getUser());

    }

    public List<Hakemus> findAll() {
        return selectDao().findAll(userHolder.getUser());
    }
}