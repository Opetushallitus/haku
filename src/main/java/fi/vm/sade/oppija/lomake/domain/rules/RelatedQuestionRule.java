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

package fi.vm.sade.oppija.lomake.domain.rules;

import fi.vm.sade.oppija.lomake.domain.elements.Element;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RelatedQuestionRule extends Rule {


    private final String expression;
    private final String relatedElementId;

    public RelatedQuestionRule(@JsonProperty(value = "id") String id,
                               @JsonProperty(value = "relatedElementId") String relatedElementId,
                               @JsonProperty(value = "expression") String expression) {
        super(id);
        this.relatedElementId = relatedElementId;
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public String getRelatedElementId() {
        return relatedElementId;
    }

    @Override
    public List<Element> getChildren(final Map<String, String> values) {
        final String value = values.get(relatedElementId);
        if (RegexRule.evaluate(value, expression)) {
            return this.getChildren();
        }
        return Collections.emptyList();
    }
}
