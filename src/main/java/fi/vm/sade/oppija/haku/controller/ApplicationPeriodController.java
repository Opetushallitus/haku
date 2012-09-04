package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.service.ApplicationPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/")
public class ApplicationPeriodController {

    final ApplicationPeriodService applicationPeriodService;

    @Autowired
    public ApplicationPeriodController(final ApplicationPeriodService applicationPeriodService) {
        this.applicationPeriodService = applicationPeriodService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public List<Map<String, Object>> listAllStudyApplications() {
        return applicationPeriodService.getApplicationPeriods();
    }

    @RequestMapping(value = "/{applicationPeriodId}", method = RequestMethod.GET, produces = "application/json")
    public Map getApplicationPeriodIdAsJson(@PathVariable final String applicationPeriodId) {
        return applicationPeriodService.getApplicationPeriod(applicationPeriodId);
    }

    @RequestMapping(value = "/{applicationPeriodId}", method = RequestMethod.GET)
    public ModelAndView getApplicationPeriodIdAsHtml(@PathVariable final String applicationPeriodId) {
        final Map<String, Object> data = applicationPeriodService.getApplicationPeriod(applicationPeriodId);
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("template");
        modelAndView.addObject("data", data);
        return modelAndView;
    }
}
