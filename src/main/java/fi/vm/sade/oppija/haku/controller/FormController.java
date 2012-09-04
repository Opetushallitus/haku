package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.service.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;


@Controller
public class FormController {

    private final static Logger logger = LoggerFactory.getLogger(FormController.class);

    final FormService formService;

    @Autowired
    public FormController(final FormService formService) {
        this.formService = formService;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}", method = RequestMethod.GET)
    public ModelAndView getFormAsHtml(@PathVariable final String applicationPeriodId, @PathVariable final String formId) {
        logger.debug("getFormsAsHtml ", applicationPeriodId, formId);
        final Map<String, Object> data = formService.getForm(applicationPeriodId, formId);
        logger.debug("form ", data);
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("form");
        modelAndView.addObject("data", data);
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}/", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> getForms(@PathVariable final String applicationPeriodId) {
        logger.debug("getForms ", applicationPeriodId);
        return formService.getForms(applicationPeriodId);
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map getFormAsJson(@PathVariable final String applicationPeriodId, @PathVariable final String formId) {
        logger.debug("getFormAsJson ", applicationPeriodId, formId);
        return formService.getForm(applicationPeriodId, formId);
    }
}
