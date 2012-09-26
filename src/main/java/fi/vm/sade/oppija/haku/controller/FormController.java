package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.service.Application;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.validation.FormValidator;
import fi.vm.sade.oppija.haku.validation.ValidationResult;
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

import javax.servlet.http.HttpSession;
import java.util.Map;


@Controller
public class FormController {

    private final static Logger logger = LoggerFactory.getLogger(FormController.class);
    public static final String DEFAULT_VIEW = "default";
    public static final String LINK_LIST_VIEW = "linkList";
    public static final String ERROR_NOTFOUND = "error/notfound";
    public static final String ERROR_SERVERERROR = "error/servererror";
    public static final String USER_ID = "userid";

    final FormService formService;

    @Autowired
    @Qualifier("applicationDAOMongoImpl")
    private ApplicationDAO applicationDAO;

    final Application application;

    @Autowired
    public FormController(@Qualifier("formServiceImpl") final FormService formService, final Application application) {
        this.formService = formService;
        this.application = application;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView listApplicationPeriods() {
        logger.debug("listApplicationPeriods");
        Map<String, ApplicationPeriod> applicationPerioidMap = formService.getApplicationPerioidMap();
        final ModelAndView modelAndView = new ModelAndView(LINK_LIST_VIEW);
        modelAndView.addObject(LINK_LIST_VIEW, applicationPerioidMap.keySet());
        return modelAndView;
    }

    /**
     * Temporary method for saving user id into session.
     * TODO: remove when authentication is implemented
     *
     * @param userid
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, params= USER_ID)
    public ModelAndView listApplicationPeriodsWithUser(@RequestParam(USER_ID) String userid, HttpSession session) {
        logger.debug("listApplicationPeriods with user: " + userid);
        session.setAttribute(USER_ID, userid);

        Map<String, ApplicationPeriod> applicationPerioidMap = formService.getApplicationPerioidMap();
        final ModelAndView modelAndView = new ModelAndView(LINK_LIST_VIEW);
        modelAndView.addObject(LINK_LIST_VIEW, applicationPerioidMap.keySet());
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}", method = RequestMethod.GET)
    public ModelAndView listForms(@PathVariable final String applicationPeriodId) {
        logger.debug("listForms");
        ApplicationPeriod applicaionPeriod = formService.getApplicationPeriodById(applicationPeriodId);
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
        modelAndView.addObject("categoryData", application.getCategoryData(categoryId));
        return modelAndView;
    }

    @RequestMapping(value = "/{applicationPeriodId}/{formId}/{categoryId}", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public ModelAndView saveCategory(@PathVariable final String applicationPeriodId,
                                     @PathVariable final String formId,
                                     @PathVariable final String categoryId,
                                     @RequestBody final MultiValueMap<String, String> multiValues,
                                     HttpSession session) {
        logger.debug("getCategory {}, {}, {}, {}", new Object[]{applicationPeriodId, formId, categoryId, multiValues});
        Map<String, String> values = multiValues.toSingleValueMap();
        application.setValue(categoryId, values);

        // TODO: remove when authentication is implemented
        //--
        if (session.getAttribute(USER_ID) != null) {
            logger.debug("posted category with userid: " + session.getAttribute(USER_ID) + " and form id: " + applicationPeriodId + "-" + formId);
            if (application.getApplicationId() == null || application.getUserId() == null) {
                application.setApplicationId(applicationPeriodId + "-" + formId);
                application.setUserId((String)session.getAttribute(USER_ID));
                logger.debug("application: " + application.getUserId());
            }
            applicationDAO.update(application);
        }
        //--

        ModelAndView modelAndView = new ModelAndView(DEFAULT_VIEW);

        FormValidator formValidator = new FormValidator();
        ValidationResult validationResult = formValidator.validate(values, formService.getCategoryValidators(applicationPeriodId, formId, categoryId));
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        Category category = getNextCategory(categoryId, values, activeForm, validationResult);
        if (!validationResult.hasErrors()) {
            modelAndView = new ModelAndView("redirect:/fi/" + applicationPeriodId + "/" + formId + "/" + category.getId());
        } else {
            modelAndView.addObject("validationResult", validationResult);
            modelAndView.addObject("category", activeForm.getCategory(categoryId));
            modelAndView.addObject("form", activeForm);
            modelAndView.addObject("categoryData", application.getCategoryData(categoryId));
        }
        return modelAndView;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView resourceNotFoundExceptions(ResourceNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView(ERROR_NOTFOUND);
        modelAndView.addObject("stackTrace", ExceptionUtils.getFullStackTrace(e));
        modelAndView.addObject("message", e.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(Throwable.class)
    public ModelAndView exceptions(Throwable t) {
        ModelAndView modelAndView = new ModelAndView(ERROR_SERVERERROR);
        modelAndView.addObject("stackTrace", ExceptionUtils.getFullStackTrace(t));
        modelAndView.addObject("message", t.getMessage());
        return modelAndView;
    }

    private Category getNextCategory(final String categoryId, final Map<String, String> values, final Form activeForm, ValidationResult errors) {
        Category category = activeForm.getCategory(categoryId);
        if (!errors.hasErrors()) {
            category = selectNextPrevOrCurrent(values, category);
        }
        return category;
    }

    private Category selectNextPrevOrCurrent(Map<String, String> values, Category category) {
        if (values.get("nav-next") != null && category.isHasNext()) {
            return category.getNext();
        } else if (values.get("nav-prev") != null && category.isHasPrev()) {
            return category.getPrev();
        }
        return category;
    }


}
