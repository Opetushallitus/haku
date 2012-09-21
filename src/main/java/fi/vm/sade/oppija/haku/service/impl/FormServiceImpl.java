package fi.vm.sade.oppija.haku.service.impl;


import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.service.FormModelHolder;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.validation.Validator;
import fi.vm.sade.oppija.haku.validation.validators.RequiredFieldValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FormServiceImpl implements FormService {


    private final FormModelHolder holder;

    @Autowired
    public FormServiceImpl(final FormModelHolder holder) {
        this.holder = holder;
    }

    private FormModel getModel() {
        FormModel model = holder.getModel();
        if (model == null) {
            if (model == null) throw new ResourceNotFoundException("Model not found");
        }
        return model;
    }

    @Override
    public Form getActiveForm(String applicationPeriodId, String formId) {
        FormModel model = getModel();
        ApplicationPeriod applicationPeriod = model.getApplicationPeriodById(applicationPeriodId);
        if (applicationPeriod == null) throw new ResourceNotFoundException("not found");
        if (!applicationPeriod.isActive()) throw new ResourceNotFoundException("Not active");
        return applicationPeriod.getFormById(formId);
    }

    @Override
    public Category getFirstCategory(String applicationPeriodId, String formId) {
        Category firstCategory = getActiveForm(applicationPeriodId, formId).getFirstCategory();
        if (firstCategory == null) {
            throw new ResourceNotFoundException("First category not found");
        }
        return firstCategory;
    }

    @Override
    public Map<String, ApplicationPeriod> getApplicationPerioidMap() {
        FormModel model = getModel();
        return model.getApplicationPerioidMap();
    }

    @Override
    public ApplicationPeriod getApplicationPeriodById(String applicationPeriodId) {
        return getModel().getApplicationPeriodById(applicationPeriodId);
    }

    @Override
    public Map<String, Validator> getCategoryValidators(String applicationPeriodId, String formId, String categoryId) {
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator("Sukunimi on pakollinen kenttä", "Sukunimi");
        HashMap<String, Validator> validators = new HashMap<String, Validator>();
        if ("henkilotiedot".equals(categoryId)) {
            validators.put(requiredFieldValidator.fieldName, requiredFieldValidator);
        }
        return validators;
    }


}
