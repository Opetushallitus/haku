package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.service.StudyApplicationService;
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
public class StudyApplicationController {

    final StudyApplicationService studyApplicationService;

    @Autowired
    public StudyApplicationController(final StudyApplicationService studyApplicationService) {
        System.out.println("StudyApplicationController");
        this.studyApplicationService = studyApplicationService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public List<Map<String, Object>> listAllStudyApplications() {
        System.out.println("getStudyApplicationAsHtml");
        return studyApplicationService.listStudyApplication();
    }

    @RequestMapping(value = "/{studyApplicationId}", method = RequestMethod.GET, produces = "application/json")
    public Map getStudyApplicationAsJson(@PathVariable final String studyApplicationId) {
        System.out.println("getStudyApplicationAsJson");
        return studyApplicationService.getStudyApplication(studyApplicationId);
    }

    @RequestMapping(value = "/{studyApplicationId}", method = RequestMethod.GET)
    public ModelAndView getStudyApplicationAsHtml(@PathVariable final String studyApplicationId) {
        System.out.println("getStudyApplicationAsHtml");
        final Map<String, Object> studyApplication = studyApplicationService.getStudyApplication(studyApplicationId);
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("template");
        modelAndView.addObject("studyApplication", studyApplication);
        return modelAndView;
    }
}
