package fi.vm.sade.oppija.haku.domain;

import java.io.Serializable;

/**
 * @author jukka
 * @version 10/12/123:21 PM}
 * @since 1.1
 */
public class User implements Serializable {

    private static final long serialVersionUID = -989381286044396123L;

    public User(String userName) {
        this.userName = userName;
    }

    private String userName;

    public String getUserName() {
        return userName;
    }
}
