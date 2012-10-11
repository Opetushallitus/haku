package fi.vm.sade.oppija.haku.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * @author jukka
 * @version 10/11/128:55 AM}
 * @since 1.1
 */

@Controller
@RequestMapping(value = "/", method = RequestMethod.GET)
public class RootController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getFrontPage() {
        return new ModelAndView("index");
    }


    @RequestMapping(value = "/fi", method = RequestMethod.GET)
    public ModelAndView selectLocale() {

        final ModelAndView modelAndView = new ModelAndView("locale");
        modelAndView.addObject("locale", "fi");
        return modelAndView;
    }

    @RequestMapping(value = "/en", method = RequestMethod.GET)
    public ModelAndView selectEnLocale(HttpSession session) {
        return new ModelAndView("locale");
    }

    @RequestMapping(value = "/sv", method = RequestMethod.GET)
    public ModelAndView selectSVLocale(HttpSession session) {
        return new ModelAndView("locale");
    }

}
