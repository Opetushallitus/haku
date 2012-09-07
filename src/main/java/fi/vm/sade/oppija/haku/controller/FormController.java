package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.domain.QuestionGroup;
import fi.vm.sade.oppija.haku.domain.questions.CheckBox;
import fi.vm.sade.oppija.haku.domain.questions.Radio;
import fi.vm.sade.oppija.haku.domain.questions.TextArea;
import fi.vm.sade.oppija.haku.domain.questions.TextQuestion;
import fi.vm.sade.oppija.haku.model.FormModel;
import fi.vm.sade.oppija.haku.service.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.awt.*;
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

    @RequestMapping(value = "/blaa", method = RequestMethod.GET)
    public ModelAndView getTest() {
        logger.debug("getApplicationPeriod");

        QuestionGroup questionGroup = new QuestionGroup("1");
        questionGroup.setTitle("Hello World");

        QuestionGroup questionGroup2 = new QuestionGroup("2");
        questionGroup2.setTitle("Hello World2");

        questionGroup.addChild(questionGroup2);
        TextQuestion textQuestion = new TextQuestion("4");
        textQuestion.setTitle("Etunimi");

        questionGroup2.addChild(textQuestion);
        questionGroup2.addChild(textQuestion);
        questionGroup2.addChild(textQuestion);
        questionGroup2.addChild(textQuestion);
        questionGroup2.addChild(textQuestion);
        questionGroup2.addChild(textQuestion);
        questionGroup2.addChild(textQuestion);
        questionGroup2.addChild(textQuestion);
        questionGroup.addChild(textQuestion);
        questionGroup.addChild(textQuestion);
        questionGroup.addChild(textQuestion);
        questionGroup.addChild(textQuestion);
        questionGroup.addChild(textQuestion);
        questionGroup.addChild(textQuestion);
        questionGroup.addChild(textQuestion);
        questionGroup.addChild(textQuestion);
        questionGroup.addChild(textQuestion);
        TextArea textArea = new TextArea("34");
        textArea.setTitle("Tekstiä");
        questionGroup.addChild(textArea);

        CheckBox checkBox = new CheckBox("sdfsadf");
        checkBox.setTitle("klsdlk");
        checkBox.addOption("voi", "vittu");
        checkBox.addOption("mansikka", "jäätelö");
        questionGroup.addChild(checkBox);
        questionGroup2.addChild(checkBox);
        checkBox.addOption("voi", "vittu");
        checkBox.addOption("mansikka", "jäätelö");
        Radio radio = new Radio("sdfsdf");
        radio.setTitle("Sukupuoli");
        radio.addOption("mies", "Mies");
        radio.addOption("nainen", "Nainen");
        questionGroup.addChild(radio);
        questionGroup2.addChild(radio);

        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("elements/QuestionGroup");
        modelAndView.addObject("element", questionGroup);
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}", method = RequestMethod.GET)
    public ModelAndView getApplicationPeriod(@PathVariable final String applicationPeriodId) {
        logger.debug("getApplicationPeriod {}", applicationPeriodId);
        final Map<String, Object> data = formService.getApplicationPeriod(applicationPeriodId);
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("form");
        modelAndView.addObject("data", data);
        final Map<String, Object> form = (Map<String, Object>) data.get("form");
        List<Map<String, Object>> categories = (List<Map<String, Object>>) form.get("categories");
        modelAndView.addObject("categories", categories);
        modelAndView.addObject("questions", categories.get(0).get("questions"));
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}", method = RequestMethod.GET)
    public String getForm(@PathVariable final String applicationPeriodId, @PathVariable final String formId) {
        logger.debug("getForm {}, {}", new Object[]{applicationPeriodId, formId});
        final Map<String, Object> firstCategory = formService.findFirstCategory(applicationPeriodId, formId);
        return "redirect:" + formId + "/" + firstCategory.get("id");
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.GET)
    public ModelAndView getCategory(@PathVariable final String applicationPeriodId,
                                    @PathVariable final String formId,
                                    @PathVariable final String categoryId) {
        logger.debug("getCategory {}, {}, {}", new Object[]{applicationPeriodId, formId, categoryId});
        final Map<String, Object> data = formService.findForm(applicationPeriodId, formId);
        final List<Map<String, Object>> categories = (List<Map<String, Object>>) data.get("categories");

        final ModelAndView model = new ModelAndView("category");
        model.addObject("formModel", new FormModel(categories, categoryId));
        return model;
    }


}
