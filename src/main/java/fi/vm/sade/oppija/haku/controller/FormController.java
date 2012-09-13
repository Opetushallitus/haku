package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.service.FormService;
import org.codehaus.plexus.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;


@Controller
public class FormController {

    private final static Logger logger = LoggerFactory.getLogger(FormController.class);
    public static final String DEFAULT_VIEW = "default";
    public static final String LINK_LIST_VIEW = "linkList";

    final FormService formService;

    @Autowired
    public FormController(@Qualifier("formServiceImpl") final FormService formService) {
        this.formService = formService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView listApplicationPeriods() {
        logger.debug("listApplicationPeriods");
        Map<String, ApplicationPeriod> applicationPerioidMap = formService.getModel().getApplicationPerioidMap();
        final ModelAndView modelAndView = new ModelAndView(LINK_LIST_VIEW);
        modelAndView.addObject(LINK_LIST_VIEW, applicationPerioidMap.keySet());
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}", method = RequestMethod.GET)
    public ModelAndView listForms(@PathVariable final String applicationPeriodId) {
        logger.debug("listForms");
        ApplicationPeriod applicaionPeriod = formService.getModel().getApplicationPeriodById(applicationPeriodId);
        final ModelAndView modelAndView = new ModelAndView(LINK_LIST_VIEW);
        modelAndView.addObject("path", applicaionPeriod.getId() + "/");
        modelAndView.addObject(LINK_LIST_VIEW, applicaionPeriod.getFormIds());
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}", method = RequestMethod.GET)
    public String getFormAndRedirectToFirstCategory(@PathVariable final String applicationPeriodId, @PathVariable final String formId) {
        logger.debug("getFormAndRedirectToFirstCategory {}, {}", new Object[]{applicationPeriodId, formId});
        Category firstCategory = formService.getFirstCategory(applicationPeriodId, formId);
        return "redirect:" + formId + "/" + firstCategory.getId();
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.GET)
    public ModelAndView getCategory(@PathVariable final String applicationPeriodId,
                                    @PathVariable final String formId,
                                    @PathVariable final String categoryId) {
        logger.debug("getCategory {}, {}, {}", new Object[]{applicationPeriodId, formId, categoryId});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        final ModelAndView modelAndView = new ModelAndView(DEFAULT_VIEW);
        modelAndView.addObject("category", activeForm.getCategory(categoryId));
        modelAndView.addObject("form", activeForm);
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public String saveCategory(@PathVariable final String applicationPeriodId,
                               @PathVariable final String formId,
                               @PathVariable final String categoryId,
                               @RequestBody final MultiValueMap<String, String> values) {
        logger.debug("getCategory {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, categoryId, values});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        Category category = activeForm.getCategory(categoryId);
        String nextId;
        if (category.isHasNext()) {
            nextId = category.getNext().getId();
        } else {
            nextId = activeForm.getFirstCategory().getId();
        }
        return "redirect:/fi/" + applicationPeriodId + "/" + formId + "/" + nextId;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFoundExceptions(ResourceNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("error/notfound");
        modelAndView.addObject("stackTrace", ExceptionUtils.getFullStackTrace(e));
        modelAndView.addObject("message", e.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(Throwable.class)
    public ModelAndView handleExceptions(Throwable t) {
        ModelAndView modelAndView = new ModelAndView("error/servererror");
        modelAndView.addObject("stackTrace", ExceptionUtils.getFullStackTrace(t));
        modelAndView.addObject("message", t.getMessage());
        return modelAndView;
    }


}
