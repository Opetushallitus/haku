package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.Attribute;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.validation.Validator;
import fi.vm.sade.oppija.haku.validation.validators.RegexFieldValidator;
import fi.vm.sade.oppija.haku.validation.validators.RequiredFieldValidator;

import java.util.*;

public class FormModelInitializer {
    private final FormModel model;

    public FormModelInitializer(FormModel model) {
        this.model = model;
    }

    public void initModel() {
        //init forms
        for (Map.Entry<String, ApplicationPeriod> stringApplicationPeriodEntry : model.getApplicationPerioidMap().entrySet()) {
            final Set<Map.Entry<String, Form>> entries = stringApplicationPeriodEntry.getValue().getForms().entrySet();
            for (Map.Entry<String, Form> entry : entries) {
                entry.getValue().init();
            }
        }
    }

    public ValidatorContainer collectValidators() {
        Map<String, ApplicationPeriod> applicationPerioidMap = model.getApplicationPerioidMap();
        ValidatorContainer validatorContainer = new ValidatorContainer();
        Collection<ApplicationPeriod> values = applicationPerioidMap.values();
        for (ApplicationPeriod value : values) {
            Map<String, Form> forms = value.getForms();
            for (Form form : forms.values()) {
                traverse(form.getChildren(), null, validatorContainer);
            }
        }
        return validatorContainer;
    }

    private void traverse(final List<Element> children, final String currentCategory, final ValidatorContainer validatorContainer) {
        for (Element child : children) {
            if (child.getType().equals("Category")) {
                traverse(child.getChildren(), child.getId(), validatorContainer);
            } else {
                Set<Attribute> attributes = child.getAttributes();
                List<Validator> validators = createValidators(child.getId(), attributes);
                validatorContainer.addValidator(currentCategory, validators);
                traverse(child.getChildren(), currentCategory, validatorContainer);
            }
        }
    }

    private List<Validator> createValidators(final String id, Set<Attribute> attributes) {
        List<Validator> validators = new ArrayList<Validator>();
        for (Attribute attribute : attributes) {
            if (attribute.getKey().equals("required")) {
                validators.add(new RequiredFieldValidator(id));
            } else if (attribute.getKey().equals("pattern")) {
                validators.add(new RegexFieldValidator(id, attribute.getValue()));
            }
        }
        return validators;
    }
}
