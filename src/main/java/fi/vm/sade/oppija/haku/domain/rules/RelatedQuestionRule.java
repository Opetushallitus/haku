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

package fi.vm.sade.oppija.haku.domain.rules;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.domain.elements.ValidatorFinder;
import fi.vm.sade.oppija.haku.validation.ConditionalValidator;
import fi.vm.sade.oppija.haku.validation.Validator;

import java.util.List;

/**
 * @author jukka
 * @version 9/20/124:49 PM}
 * @since 1.1
 */
public class RelatedQuestionRule extends Rule {


    private final String expression;

    public RelatedQuestionRule(@JsonProperty(value = "id") String id, @JsonProperty(value = "expression") String expression) {
        super(id);
        this.expression = expression;
    }

    public void setRelated(Element parent, Element target) {
        getChildById().put(parent.getId(), target);
        children.add(target);
    }

    public String getExpression() {
        return expression;
    }

    @Override
    protected boolean isValidating() {
        return true;
    }

    @Override
    protected void initValidators() {
        final List<Validator> validatingParent = new ValidatorFinder(parent, Vaihe.class).findValidatingParentValidators();
        final ConditionalValidator conditionalValidator = new ConditionalValidator(this);
        for (Validator validator : validators) {
            conditionalValidator.add(validator);
        }
        validatingParent.add(conditionalValidator);
    }
}
