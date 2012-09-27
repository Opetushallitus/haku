package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @author jukka
 * @version 9/27/129:21 AM}
 * @since 1.1
 */
@Component("sessionDataHolder")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionDataHolder implements Serializable, ApplicationDAO {

    private HashMap<HakemusId, Hakemus> map = new HashMap<HakemusId, Hakemus>();

    public Hakemus find(HakemusId id) {
        Hakemus hakemus = map.get(id);
        if (hakemus == null) {
            hakemus = new Hakemus(id, new HashMap<String, String>());
            map.put(id, hakemus);
        }
        return hakemus;
    }

    @Override
    public List<Application> findAllByUserId(String userId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Application find(String userId, String applicationId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void update(Application application) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void update(Hakemus hakemus) {
        this.map.put(hakemus.getHakemusId(), hakemus);
    }
}
