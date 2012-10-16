/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.Attribute;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.validation.Validator;
import fi.vm.sade.oppija.haku.validation.validators.RegexFieldValidator;
import fi.vm.sade.oppija.haku.validation.validators.RequiredFieldValidator;

import java.util.*;

public class ValidatorCollector {
    private final Map<String, ApplicationPeriod> applicationPerioids;

    public ValidatorCollector(final Map<String, ApplicationPeriod> applicationPerioids) {
        this.applicationPerioids = Collections.unmodifiableMap(applicationPerioids);
    }

    public ValidatorContainer collect() {
        ValidatorContainer validatorContainer = new ValidatorContainer();
        Collection<ApplicationPeriod> values = this.applicationPerioids.values();
        for (ApplicationPeriod value : values) {
            Map<String, Form> forms = value.getForms();
            for (Form form : forms.values()) {
                traverse(new TraverseParameters(form.getChildren(), validatorContainer));
            }
        }
        return validatorContainer;
    }

    private void traverse(TraverseParameters traverseParameters) {
        for (Element child : traverseParameters.getChildren()) {
            if (child.getType().equals("Vaihe")) {
                traverse(new TraverseParameters(child.getChildren(), child.getId(), traverseParameters.getValidatorContainer()));
            } else {
                Collection<Attribute> attributes = child.getAttributes().values();
                List<Validator> validators = createValidators(child.getId(), attributes);
                traverseParameters.getValidatorContainer().addValidator(traverseParameters.getCurrentCategory(), validators);
                traverse(new TraverseParameters(child.getChildren(), traverseParameters.getCurrentCategory(), traverseParameters.getValidatorContainer()));
            }
        }
    }

    private List<Validator> createValidators(final String id, Collection<Attribute> attributes) {
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

    private class TraverseParameters {
        private final List<Element> children;
        private final String currentCategory;
        private final ValidatorContainer validatorContainer;

        TraverseParameters(final List<Element> children, final ValidatorContainer validatorContainer) {
            this.children = children;
            this.currentCategory = null;
            this.validatorContainer = validatorContainer;
        }

        TraverseParameters(final List<Element> children, final String currentCategory, final ValidatorContainer validatorContainer) {
            this.children = children;
            this.currentCategory = currentCategory;
            this.validatorContainer = validatorContainer;
        }

        public List<Element> getChildren() {
            return children;
        }

        public String getCurrentCategory() {
            return currentCategory;
        }

        public ValidatorContainer getValidatorContainer() {
            return validatorContainer;
        }
    }
}
