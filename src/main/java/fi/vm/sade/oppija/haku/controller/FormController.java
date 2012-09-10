package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.domain.*;
import fi.vm.sade.oppija.haku.domain.questions.TextQuestion;
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

    @RequestMapping(value = "/blaa", method = RequestMethod.GET)
    public ModelAndView getTest() {
        logger.debug("getApplicationPeriod");
        ApplicationPeriod applicationPeriod = createTestModel();
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("form");
        modelAndView.addObject("form", applicationPeriod.getFormById("111"));
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}", method = RequestMethod.GET)
    public ModelAndView getApplicationPeriod(@PathVariable final String applicationPeriodId) {
        logger.debug("getApplicationPeriod {}", applicationPeriodId);
        final ApplicationPeriod activePeriodById = formService.getModel().getActivePeriodById(applicationPeriodId);
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("form");
        modelAndView.addObject("data", activePeriodById);
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}", method = RequestMethod.GET)
    public String getForm(@PathVariable final String applicationPeriodId, @PathVariable final String formId) {
        logger.debug("getForm {}, {}", new Object[]{applicationPeriodId, formId});
        //final Form formById = formService.getModel().getActivePeriodById(applicationPeriodId).getFormById(formId);
        ApplicationPeriod testModel = createTestModel();

        Form formById = testModel.getFormById(formId);
        return "redirect:" + formId + "/" + formById.getFirstCategory().getId();
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.GET)
    public ModelAndView getCategory(@PathVariable final String applicationPeriodId,
                                    @PathVariable final String formId,
                                    @PathVariable final String categoryId) {
        logger.debug("getCategory {}, {}, {}", new Object[]{applicationPeriodId, formId, categoryId});
        final ModelAndView model = new ModelAndView("form");
        ApplicationPeriod testModel = createTestModel();
        Form formById = testModel.getFormById(formId);
        model.addObject("category", formById.getCategory(categoryId));
        model.addObject("form", formById);
        return model;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public String saveCategory(@PathVariable final String applicationPeriodId,
                               @PathVariable final String formId,
                               @PathVariable final String categoryId,
                               @RequestBody final MultiValueMap<String, String> values) {
        logger.debug("getCategory {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, categoryId, values.size()});
        ApplicationPeriod testModel = createTestModel();
        Form formById = testModel.getFormById(formId);
        Category category = formById.getCategory(categoryId);
        String nextId;
        if (category.isHasNext()) {
            nextId = category.getNext().getId();
        } else {
            nextId = formById.getFirstCategory().getId();
        }
        return "redirect:/fi/" + applicationPeriodId + "/" + formId + "/" + nextId;
    }

    private ApplicationPeriod createTestModel() {
        ApplicationPeriod applicationPeriod = new ApplicationPeriod("0");
        Category category1 = new Category("222", "Henkilötiedot");
        Category category2 = new Category("223", "Koulutustausta");
        Category category3 = new Category("224", "Hakutoiveet");
        Category category4 = new Category("225", "Osaaminen");
        Form form = new Form("yhteishaku", "yhteishaku");
        form.addChild(category1);
        form.addChild(category2);
        form.addChild(category3);
        form.addChild(category4);
        form.produceCategoryMap();

        applicationPeriod.addForm(form);

        QuestionGroup questionGroup = new QuestionGroup("1", "Henkilötiedot");
        category1.addChild(questionGroup);
        category2.addChild(questionGroup);
        category3.addChild(questionGroup);
        category4.addChild(questionGroup);

        TextQuestion äidinkieli = new TextQuestion("15", "Äidinkieli", "Äidinkieli");
        äidinkieli.setHelp("Minkä värinen on äitisi kieli?");
        questionGroup.addChild(new TextQuestion("2", "Sukunimi", "Sukunimi"))
                .addChild(createRequiredTextQuestion("3", "Etunimi"))
                .addChild(new TextQuestion("4", "Kutsumanimi", "Kutsumanimi"))
                .addChild(new TextQuestion("5", "Henkilötunnus", "Henkilötunnus"))
                .addChild(new TextQuestion("6", "Sukupuoli", "Sukupuoli"))
                .addChild(new TextQuestion("8", "Sähköposti", "Sähköposti"))
                .addChild(new TextQuestion("9", "Matkapuhelinnumero", "Matkapuhelinnumero"))
                .addChild(new TextQuestion("10", "Asuinmaa", "Asuinmaa"))
                .addChild(new TextQuestion("11", "Lähiosoite", "Lähiosoite"))
                .addChild(new TextQuestion("12", "Postinumero", "Postinumero"))
                .addChild(new TextQuestion("13", "Kotikunta", "Kotikunta"))
                .addChild(new TextQuestion("14", "Kansalaisuus", "Kansalaisuus"))
                .addChild(äidinkieli);

        return applicationPeriod;
    }

    private Element createRequiredTextQuestion(final String id, final String name) {
        TextQuestion textQuestion = new TextQuestion(id, name, name);
        textQuestion.addAttribute("required", "required");
        return textQuestion;
    }


}
