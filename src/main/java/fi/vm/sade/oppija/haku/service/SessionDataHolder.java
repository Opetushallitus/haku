package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author jukka
 * @version 9/27/129:21 AM}
 * @since 1.1
 */
@Component("sessionDataHolder")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionDataHolder implements Serializable, ApplicationDAO {

    private static final long serialVersionUID = -3751714345380438532L;
    private HashMap<HakemusId, Hakemus> map = new HashMap<HakemusId, Hakemus>();

    public Hakemus find(HakemusId id) {
        Hakemus hakemus = map.get(id);
        if (hakemus == null) {
            hakemus = new Hakemus(id, new HashMap<String, String>());
            map.put(id, hakemus);
        }
        return hakemus;
    }

    public void update(Hakemus hakemus) {
        this.map.put(hakemus.getHakemusId(), hakemus);
    }
}
