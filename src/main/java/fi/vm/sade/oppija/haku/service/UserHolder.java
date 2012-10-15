package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.AnonymousUser;
import fi.vm.sade.oppija.haku.domain.User;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author jukka
 * @version 10/12/122:46 PM}
 * @since 1.1
 */
@Component("userHolder")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserHolder implements Serializable {

    private static final long serialVersionUID = 8093993846121110534L;

    private User user = new AnonymousUser();
    private boolean userKnown = false;

    public User getUser() {
        return user;
    }

    public boolean isUserKnown() {
        return userKnown;
    }

    public void login(User user) {
        this.user = user;
        userKnown = true;
    }
}
