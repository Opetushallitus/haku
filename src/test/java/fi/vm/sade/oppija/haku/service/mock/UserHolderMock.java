package fi.vm.sade.oppija.haku.service.mock;

import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.service.UserHolder;

/**
 * @author Hannu Lyytikainen
 */
public class UserHolderMock extends UserHolder {

    private User user;

    public UserHolderMock(String username) {
        this.user = new User(username);
    }

    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    public boolean isUserKnown() {
        return true;
    }
}
