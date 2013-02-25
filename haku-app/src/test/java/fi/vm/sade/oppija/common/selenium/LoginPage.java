package fi.vm.sade.oppija.common.selenium;

import com.thoughtworks.selenium.Selenium;

/**
 * @author jukka
 * @version 10/15/123:55 PM}
 * @since 1.1
 */
public class LoginPage {

    private final Selenium selenium;

    public LoginPage(Selenium selenium) {
        this.selenium = selenium;
    }

    public void login(String user) {
        selenium.type("j_username", user);
        selenium.type("j_password", user);
        selenium.submit("login");
    }

}
