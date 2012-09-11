package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.domain.Category;
import fi.vm.sade.oppija.haku.domain.Form;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.service.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class FormController {

    private final static Logger logger = LoggerFactory.getLogger(FormController.class);

    final FormService formService;

    @Autowired
    public FormController(final FormService formService) {
        this.formService = formService;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}", method = RequestMethod.GET)
    public String getForm(@PathVariable final String applicationPeriodId, @PathVariable final String formId) {
        logger.debug("getForm {}, {}", new Object[]{applicationPeriodId, formId});
        Form formById = getFormById(applicationPeriodId, formId);
        return "redirect:" + formId + "/" + formById.getFirstCategory().getId();
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.GET)
    public ModelAndView getCategory(@PathVariable final String applicationPeriodId,
                                    @PathVariable final String formId,
                                    @PathVariable final String categoryId) {
        logger.debug("getCategory {}, {}, {}", new Object[]{applicationPeriodId, formId, categoryId});
        Form formById = getFormById(applicationPeriodId, formId);
        final ModelAndView modelAndView = new ModelAndView("form");
        modelAndView.addObject("category", formById.getCategory(categoryId));
        modelAndView.addObject("form", formById);
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public String saveCategory(@PathVariable final String applicationPeriodId,
                               @PathVariable final String formId,
                               @PathVariable final String categoryId,
                               @RequestBody final MultiValueMap<String, String> values) {
        logger.debug("getCategory {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, categoryId, values.size()});
        Form formById = getFormById(applicationPeriodId, formId);
        Category category = formById.getCategory(categoryId);
        String nextId;
        if (category.isHasNext()) {
            nextId = category.getNext().getId();
        } else {
            nextId = formById.getFirstCategory().getId();
        }
        return "redirect:/fi/" + applicationPeriodId + "/" + formId + "/" + nextId;
    }

    private Form getFormById(String applicationPeriodId, String formId) {
        FormModel model = formService.getModel();
        return model.getActivePeriodById(applicationPeriodId).getFormById(formId);
    }


}
