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
import fi.vm.sade.oppija.lomake.domain.rules.expression.Expr;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RelatedQuestionComplexRule extends Element {

    private static final long serialVersionUID = -6030200061901263949L;
    private final Expr expr;
    private final String exprJsonStr;

    public RelatedQuestionComplexRule(@JsonProperty(value = "id") String id,
                                      @JsonProperty(value = "expr") final Expr expr) {
        super(id);
        this.expr = expr;
        ObjectMapper mapper = new ObjectMapper();
        String tmp = "";
        try {
            tmp = mapper.writeValueAsString(this.expr);
        } catch (IOException e) {
        }
        this.exprJsonStr = tmp;
    }

    @Override
    public List<Element> getChildren(final Map<String, String> values) {
        if (expr.evaluate(values)) {
            return getChildren();
        }
        return Collections.emptyList();

    }

    @JsonIgnore
    public String getExprJsonStr() {
        return exprJsonStr;
    }
}
