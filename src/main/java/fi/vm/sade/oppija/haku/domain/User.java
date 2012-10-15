package fi.vm.sade.oppija.haku.domain;

/**
 * @author jukka
 * @version 10/12/123:21 PM}
 * @since 1.1
 */
public class User {

    public User(String userName) {
        this.userName = userName;
    }

    private String userName;

    public String getUserName() {
        return userName;
    }
}
