package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.service.StudyApplicationProcessService;
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
public class StudyApplicationProcessController {

    final StudyApplicationProcessService studyApplicationProcessService;

    @Autowired
    public StudyApplicationProcessController(final StudyApplicationProcessService studyApplicationProcessService) {
        this.studyApplicationProcessService = studyApplicationProcessService;
    }

    @RequestMapping(value = "/{studyApplicationId}/{studyApplicationProcessId}", method = RequestMethod.GET)
    public ModelAndView getStudyApplicationProcessAsHtml(@PathVariable final String studyApplicationId, @PathVariable final String studyApplicationProcessId) {
        System.out.println("getStudyApplicationProcessAsHtml");
        final Map<String, Object> studyApplicationProcess = studyApplicationProcessService.getStudyApplicationProcess(studyApplicationId, studyApplicationProcessId);
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("template");
        modelAndView.addObject("studyApplication", studyApplicationProcess);
        return modelAndView;
    }

    @RequestMapping(value = "/{studyApplicationId}/", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> listAllStudyApplicationProcesses(@PathVariable final String studyApplicationId) {
        return studyApplicationProcessService.listStudyApplicationProcesses(studyApplicationId);
    }

    @RequestMapping(value = "/{studyApplicationId}/{studyApplicationProcessId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map getStudyApplicationProcessAsJson(@PathVariable final String studyApplicationId, @PathVariable final String studyApplicationProcessId) {
        return studyApplicationProcessService.getStudyApplicationProcess(studyApplicationId, studyApplicationProcessId);
    }
}
