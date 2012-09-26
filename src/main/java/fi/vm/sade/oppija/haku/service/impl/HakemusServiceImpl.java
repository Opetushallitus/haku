package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.service.Application;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.service.HakemusService;
import fi.vm.sade.oppija.haku.validation.FormValidator;
import fi.vm.sade.oppija.haku.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/26/122:43 PM}
 * @since 1.1
 */
@Service
public class HakemusServiceImpl implements HakemusService {
    private static final Logger log = LoggerFactory.getLogger(HakemusServiceImpl.class);

    @Autowired
    private Application application;

    @Autowired
    @Qualifier("applicationDAOMongoImpl")
    private ApplicationDAO applicationDAO;

    @Autowired
    @Qualifier("formServiceImpl")
    private FormService formService;

    @Override
    public void save(Hakemus hakemus) {
        application.setValue(hakemus.getHakemusId().getCategoryId(), hakemus.getValues());
        ValidationResult validationResult = validateAndSave(hakemus);
        hakemus.setValidationResult(validationResult);
    }

    @Override
    public Hakemus getHakemus(HakemusId hakemusId) {
        return new Hakemus(hakemusId, new HashMap<String, String>());
    }

    private ValidationResult validateAndSave(Hakemus hakemus) {
        final HakemusId hakemusId = hakemus.getHakemusId();
        updateApplication(hakemusId);
        FormValidator formValidator = new FormValidator();
        ValidationResult validationResult = formValidator.validate(hakemus.getValues(), formService.getCategoryValidators(hakemusId.getApplicationPeriodId(), hakemusId.getFormId(), hakemusId.getCategoryId()));
        Form activeForm = formService.getActiveForm(hakemusId.getApplicationPeriodId(), hakemusId.getFormId());
        Category category = getNextCategory(hakemusId.getCategoryId(), hakemus.getValues(), activeForm, validationResult);
        validationResult.setCategory(category);
        return validationResult;
    }

    private void updateApplication(HakemusId hakemusId) {

        // TODO: remove when authentication is implemented
        //--
        if (hakemusId.getUserId() != null) {
            log.debug("posted category with userid: " + hakemusId.getUserId() + " and form id: " + hakemusId.getApplicationPeriodId() + "-" + hakemusId.getFormId());
            if (application.getApplicationId() == null || application.getUserId() == null) {
                application.setApplicationId(hakemusId.getApplicationPeriodId() + "-" + hakemusId.getFormId());
                application.setUserId(hakemusId.getUserId());
                log.debug("application: " + application.getUserId());
            }
            applicationDAO.update(application);
        }
        //--
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
