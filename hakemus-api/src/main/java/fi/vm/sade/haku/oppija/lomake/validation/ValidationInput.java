/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.haku.oppija.lomake.validation;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;

import java.util.HashMap;
import java.util.Map;

public class ValidationInput {
    public static enum ValidationContext {
        applicant_submit, applicant_modify, officer_modify, background;
    }

    private final String applicationOid;
    private final String applicationSystemId;
    private final Element element;
    private final Map<String, String> values;
    private final ValidationContext validationContext;

    public ValidationInput(final Element element,
                           final Map<String, String> values,
                           final String applicationOid,
                           final String applicationSystemId,
                           final ValidationContext validationContext) {
        this.element = element;
        this.values = new HashMap<String, String>(values);
        this.applicationOid = applicationOid;
        this.applicationSystemId = applicationSystemId;
        this.validationContext = validationContext;
    }

    public ValidationInput(final Element element, ValidationInput validationInput) {
        this(element,
                validationInput.getValues(),
                validationInput.getApplicationOid(),
                validationInput.getApplicationSystemId(),
                validationInput.getValidationContext());
    }

    public Element getElement() {
        return element;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public boolean containsKey(final String key) {
        return values.containsKey(key);
    }

    public String getValueByKey(final String key) {
        return values.get(key);
    }

    public String getValue() {
        return values.get(element.getId());
    }

    public String getApplicationOid() {
        return applicationOid;
    }

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    public String getFieldName() {
        return this.element.getId();
    }

    public ValidationContext getValidationContext() {
        return validationContext;
    }
}
