package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.domain.Category;
import fi.vm.sade.oppija.haku.domain.Form;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.service.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class FormController {

    private final static Logger logger = LoggerFactory.getLogger(FormController.class);

    final FormService formService;

    @Autowired
    public FormController(@Qualifier("formServiceImpl") final FormService formService) {
        this.formService = formService;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}", method = RequestMethod.GET)
    public String getForm(@PathVariable final String applicationPeriodId, @PathVariable final String formId) {
        logger.debug("getForm {}, {}", new Object[]{applicationPeriodId, formId});
        Category firstCategory = formService.getFirstCategory(applicationPeriodId, formId);
        return "redirect:" + formId + "/" + firstCategory.getId();
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.GET)
    public ModelAndView getCategory(@PathVariable final String applicationPeriodId,
                                    @PathVariable final String formId,
                                    @PathVariable final String categoryId) {
        logger.debug("getCategory {}, {}, {}", new Object[]{applicationPeriodId, formId, categoryId});
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        final ModelAndView modelAndView = new ModelAndView("form");
        modelAndView.addObject("category", activeForm.getCategory(categoryId));
        modelAndView.addObject("form", activeForm);
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public String saveCategory(@PathVariable final String applicationPeriodId,
                               @PathVariable final String formId,
                               @PathVariable final String categoryId,
                               @RequestBody final MultiValueMap<String, String> values) {
        logger.debug("getCategory {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, categoryId, values.size()});
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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleAllExceptions(Throwable t) {
        return new ModelAndView("error");
    }


}
