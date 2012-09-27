package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.service.HakemusService;
import fi.vm.sade.oppija.haku.service.SessionDataHolder;
import fi.vm.sade.oppija.haku.validation.FormValidator;
import fi.vm.sade.oppija.haku.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author jukka
 * @version 9/26/122:43 PM}
 * @since 1.1
 */
@Service
public class HakemusServiceImpl implements HakemusService {
    private static final Logger LOG = LoggerFactory.getLogger(HakemusServiceImpl.class);

    private final ApplicationDAO sessionDataHolder;
    private final ApplicationDAO applicationDAO;
    private final FormService formService;

    @Autowired
    public HakemusServiceImpl(@Qualifier("sessionDataHolder") SessionDataHolder sessionDataHolder, @Qualifier("applicationDAOMongoImpl") ApplicationDAO applicationDAO, @Qualifier("formServiceImpl") FormService formService) {
        this.sessionDataHolder = sessionDataHolder;
        this.applicationDAO = applicationDAO;
        this.formService = formService;
    }

    @Override
    public void save(Hakemus hakemus) {
        ValidationResult validationResult = validate(hakemus);
        updateApplication(hakemus);
        hakemus.setValidationResult(validationResult);
    }

    @Override
    public Hakemus getHakemus(HakemusId hakemusId) {
        return selectDao(hakemusId).find(hakemusId);
    }

    private ApplicationDAO selectDao(HakemusId hakemusId) {
        if (hakemusId.isUserKnown()) {
            return applicationDAO;
        }
        return sessionDataHolder;
    }


    private ValidationResult validate(Hakemus hakemus) {
        final HakemusId hakemusId = hakemus.getHakemusId();
        FormValidator formValidator = new FormValidator();
        ValidationResult validationResult = formValidator.validate(hakemus.getValues(), formService.getCategoryValidators(hakemusId.getApplicationPeriodId(), hakemusId.getFormId(), hakemusId.getCategoryId()));
        Form activeForm = formService.getActiveForm(hakemusId.getApplicationPeriodId(), hakemusId.getFormId());
        Category category = getNextCategory(hakemusId.getCategoryId(), hakemus.getValues(), activeForm, validationResult);
        validationResult.setCategory(category);
        return validationResult;
    }

    private void updateApplication(Hakemus hakemus) {
        selectDao(hakemus.getHakemusId()).update(hakemus);
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
