package fi.vm.sade.oppija.haku.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author jukka
 * @version 10/11/124:41 PM}
 * @since 1.1
 */
@Controller
@RequestMapping(value = "/osio/{section}", method = RequestMethod.GET)
public class SectionController {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getPage(@PathVariable("section") final String section) {
        return new ModelAndView(section);
    }
}
