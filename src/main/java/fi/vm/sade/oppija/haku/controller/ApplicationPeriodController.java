package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.service.ApplicationPeriodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger logger = LoggerFactory.getLogger(ApplicationPeriodController.class);

    final ApplicationPeriodService applicationPeriodService;

    @Autowired
    public ApplicationPeriodController(final ApplicationPeriodService applicationPeriodService) {
        this.applicationPeriodService = applicationPeriodService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public List<Map<String, Object>> getApplicationPeriods() {
        logger.debug("getApplicationPeriods");
        return applicationPeriodService.getApplicationPeriods();
    }

    @RequestMapping(value = "/{applicationPeriodId}", method = RequestMethod.GET, produces = "application/json")
    public Map getApplicationPeriodAsJson(@PathVariable final String applicationPeriodId) {
        logger.debug("getApplicationPeriodAsJson ", applicationPeriodId);
        return applicationPeriodService.getApplicationPeriod(applicationPeriodId);
    }

    @RequestMapping(value = "/{applicationPeriodId}", method = RequestMethod.GET)
    public ModelAndView getApplicationPeriodAsHtml(@PathVariable final String applicationPeriodId) {
        logger.debug("getApplicationPeriodAsHtml ", applicationPeriodId);
        final Map<String, Object> data = applicationPeriodService.getApplicationPeriod(applicationPeriodId);
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("form");
        modelAndView.addObject("data", data);
        final Map<String, Object> form = (Map<String, Object>) data.get("form");
        List<Map<String, Object>> categories = (List<Map<String, Object>>) form.get("categories");
        modelAndView.addObject("categories", categories);
        modelAndView.addObject("questions", categories.get(0).get("questions"));
        return modelAndView;
    }
}
