package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.service.CategoryService;
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
public class CategoryController {

    final CategoryService categoryService;

    @Autowired
    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.GET)
    public ModelAndView getCategoryAsHtml(@PathVariable final String applicationPeriodId,
                                          @PathVariable final String formId,
                                          @PathVariable final String categoryId) {
        final Map<String, Object> data = categoryService.getCategory(applicationPeriodId, formId, categoryId);
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("template");
        modelAndView.addObject("data", data);
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> getCategories(@PathVariable final String applicationPeriodId, @PathVariable final String formId) {
        return categoryService.getCategories(applicationPeriodId, formId);
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{CategoryId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map getStudyApplicationProcessAsJson(@PathVariable final String applicationPeriodId,
                                                @PathVariable final String formId,
                                                @PathVariable final String categoryId) {
        return categoryService.getCategory(applicationPeriodId, formId, categoryId);
    }
}
