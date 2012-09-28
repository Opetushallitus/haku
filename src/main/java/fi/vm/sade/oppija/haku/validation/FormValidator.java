package fi.vm.sade.oppija.haku.validation;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FormValidator {

    final FormService formService;

    @Autowired
    public FormValidator(@Qualifier("formServiceImpl") FormService formService) {
        this.formService = formService;
    }


    public ValidationResult validate(Hakemus hakemus) {

        final Map<String, Validator> validators = getValidators(hakemus);

        final ValidationResult validationResult = new ValidationResult(hakemus);

        for (Map.Entry<String, Validator> validatorEntry : validators.entrySet()) {
            Validator validator = validatorEntry.getValue();
            String valueAndValidatorKey = validatorEntry.getKey();
            if (!validator.validate(hakemus.getValues())) {
                validationResult.addError(valueAndValidatorKey, validator.getErrorMessage());
            }
        }

        Category category = getNextCategory(hakemus, validationResult);
        validationResult.addModelObject("category", category);
        return validationResult;
    }

    protected Map<String, Validator> getValidators(Hakemus hakemus) {
        return formService.getCategoryValidators(hakemus.getHakemusId());
    }

    private Category getNextCategory(Hakemus hakemus, ValidationResult validationResult) {
        Form activeForm = formService.getActiveForm(hakemus.getHakemusId().getApplicationPeriodId(), hakemus.getHakemusId().getFormId());

        Category category = activeForm.getCategory(hakemus.getHakemusId().getCategoryId());
        if (!validationResult.hasErrors()) {
            category = selectNextPrevOrCurrent(hakemus.getValues(), category);
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
