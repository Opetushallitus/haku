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

package fi.vm.sade.oppija.lomake.validation.validators;

import fi.vm.sade.oppija.lomake.domain.rules.RegexRule;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomake.validation.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jukka
 * @version 11/9/125:38 PM}
 * @since 1.1
 */
public class ConditionalFieldValidator extends FieldValidator {

    List<Validator> validators = new ArrayList<Validator>();
    private RelatedQuestionRule rule;

    public ConditionalFieldValidator(RelatedQuestionRule rule) {
        super(rule.getId(), " error");
        this.rule = rule;
    }

    public void add(Validator validator) {
        this.validators.add(validator);
    }

    @Override
    public ValidationResult validate(Map<String, String> values) {
        final String value = values.get(rule.getId());
        final Map<String, String> errorMessages = new HashMap<String, String>();
        if (value != null && RegexRule.evaluate(value, rule.getExpression())) {
            for (Validator validator : validators) {
                final ValidationResult validate = validator.validate(values);
                errorMessages.putAll(validate.getErrorMessages());
            }
        }
        return new ValidationResult(errorMessages);
    }


}
