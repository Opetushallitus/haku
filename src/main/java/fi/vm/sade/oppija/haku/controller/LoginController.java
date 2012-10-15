package fi.vm.sade.oppija.haku.controller;

/**
 * @author jukka
 * @version 10/12/121:20 PM}
 * @since 1.1
 */

import fi.vm.sade.oppija.haku.domain.User;
import fi.vm.sade.oppija.haku.service.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
public class LoginController {

    private final UserHolder userHolder;

    @Autowired
    public LoginController(UserHolder userHolder) {
        this.userHolder = userHolder;
    }

    @RequestMapping(value = "/postLogin", method = RequestMethod.GET)
    public String postLogin(HttpSession session, Principal principal) {

        userHolder.login(new User(principal.getName()));
        session.setAttribute("username", principal.getName());

        return "redirect:oma";

    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(ModelMap model) {
        return "top/login";

    }

    @RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
    public String loginerror(ModelMap model) {

        model.addAttribute("error", "true");
        return "login";

    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(ModelMap model) {

        return "login";

    }

}