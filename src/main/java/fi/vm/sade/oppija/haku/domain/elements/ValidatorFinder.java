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

package fi.vm.sade.oppija.haku.domain.elements;

import fi.vm.sade.oppija.haku.validation.Validator;

import java.util.List;

public class ValidatorFinder {
    private final Element element;


    public ValidatorFinder(Element element) {
        this.element = element;
    }

    /**
     * this method walks up in model hierarchy and finds nearest validating parent and returns its validators
     *
     * @return
     */
    public List<Validator> findValidatingParentValidators() {
        List<Validator> validators = element.getValidators();
        Element element = this.element;
        while (!element.isValidating() && !element.getClass().equals(Vaihe.class)) {
            element = element.parent;
            validators = element.getValidators();
        }
        return validators;
    }
}
