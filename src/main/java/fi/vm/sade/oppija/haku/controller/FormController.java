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

import java.util.Map;


@Controller
public class FormController {

    private final static Logger logger = LoggerFactory.getLogger(FormController.class);

    final ApplicationPeriodService applicationPeriodService;

    @Autowired
    public FormController(final ApplicationPeriodService applicationPeriodService) {
        this.applicationPeriodService = applicationPeriodService;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}", method = RequestMethod.GET)
    public ModelAndView getFormAsHtml(@PathVariable final String applicationPeriodId, @PathVariable final String formId) {
        logger.debug("getFormsAsHtml ", applicationPeriodId, formId);
        final Map<String, Object> data = applicationPeriodService.findForm(applicationPeriodId, formId);
        logger.debug("form ", data);
        System.out.println(data);
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("forms");
        modelAndView.addObject("data", data);
        return modelAndView;
    }

}
