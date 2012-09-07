package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.service.FormService;
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
public class TestController {

    private final static Logger logger = LoggerFactory.getLogger(TestController.class);

    final FormService formService;

    @Autowired
    public TestController(final FormService formService) {
        this.formService = formService;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView getApplicationPeriod(@PathVariable final String applicationPeriodId) {
        logger.debug("getApplicationPeriod {}", applicationPeriodId);
        final Map<String, Object> data = formService.getApplicationPeriod(applicationPeriodId);
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("root");
        modelAndView.addObject("data", data);
        final Map<String, Object> form = (Map<String, Object>) data.get("form");
        List<Map<String, Object>> categories = (List<Map<String, Object>>) form.get("categories");
        modelAndView.addObject("categories", categories);
        modelAndView.addObject("questions", categories.get(0).get("questions"));
        return modelAndView;
    }
}
