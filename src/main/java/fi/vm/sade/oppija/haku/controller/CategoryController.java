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
public class CategoryController {

    final ApplicationPeriodService applicationPeriodService;

    @Autowired
    public CategoryController(final ApplicationPeriodService applicationPeriodService) {
        this.applicationPeriodService = applicationPeriodService;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.GET)
    public ModelAndView getCategoryAsHtml(@PathVariable final String applicationPeriodId,
                                          @PathVariable final String formId,
                                          @PathVariable final String categoryId) {
        final Map<String, Object> data = applicationPeriodService.findForm(applicationPeriodId, formId);
        final List<Map<String, Object>> categories = (List<Map<String, Object>>) data.get("categories");

        final ModelAndView modelAndView = new ModelAndView();

        String prev = null;
        String next = null;

        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).get("id").equals(categoryId)) {
                modelAndView.addObject("category", categories.get(i));
                if (i > 0) {
                    prev = (String) categories.get(i - 1).get("id");
                }
                if (i < categories.size() - 1) {
                    next = (String) categories.get(i + 1).get("id");
                }
            }
        }
        modelAndView.setViewName("category");
        modelAndView.addObject("prev", prev);
        modelAndView.addObject("next", next);
        return modelAndView;
    }

}
