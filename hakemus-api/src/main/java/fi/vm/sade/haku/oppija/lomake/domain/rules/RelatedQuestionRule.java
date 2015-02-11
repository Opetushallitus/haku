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

package fi.vm.sade.haku.oppija.lomake.domain.rules;

import com.google.common.base.Preconditions;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Variable;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;

import java.util.*;

public class RelatedQuestionRule extends Element {

    private static final long serialVersionUID = -6030200061901263949L;
    private final Expr expr;

    @Transient
    private final Set<String> variables;

    @PersistenceConstructor
    public RelatedQuestionRule(@JsonProperty(value = "id") String id,
                               @JsonProperty(value = "expr") final Expr expr) {
        super(id);
        Preconditions.checkNotNull(expr);
        this.expr = expr;
        variables = getVariables(expr);
    }

    @Override
    public List<Element> getChildren(final Map<String, String> values) {
        if (expr.evaluate(values)) {
            return getChildren();
        }
        return Collections.emptyList();
    }

    @Transient
    public Set<String> getVariables() {
        return variables;
    }

    public Expr getExpr() {
        return expr;
    }

    private static Set<String> getVariables(final Expr expr) {
        HashSet<String> variableIds = new HashSet<String>();
        if (expr == null) {
            return variableIds;
        }
        if (expr instanceof Variable) {
            variableIds.add(expr.getValue());
        }
        for (Expr child: expr.children()) {
            variableIds.addAll(getVariables(child));
        }
        return variableIds;

    }
}
