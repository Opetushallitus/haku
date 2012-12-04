package fi.vm.sade.oppija.ui.controller;

import fi.vm.sade.oppija.hakemus.domain.ApplicationInfo;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author jukka
 * @version 10/12/1210:07 AM}
 * @since 1.1
 */
@Controller
@RequestMapping(value = "/oma", method = RequestMethod.GET)
@Secured("ROLE_USER")
public class PersonalServices {

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping
    public ModelAndView hautKoulutuksiin() {
        final ModelAndView modelAndView = new ModelAndView("personal/haut");
        final List<ApplicationInfo> all = applicationService.findAll();
        modelAndView.addObject("hakemusList", all);
        modelAndView.addObject("hakemusListSize", all.size());
        return modelAndView;
    }

}
