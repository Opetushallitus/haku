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

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Group;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RelatedQuestionRule extends Element {

    private static final long serialVersionUID = -6030200061901263949L;
    private final String expression;
    private final List<String> relatedElementId;
    private final boolean showImmediately;

    @PersistenceConstructor
    public RelatedQuestionRule(@JsonProperty(value = "id") String id,
                               @JsonProperty(value = "relatedElementId") List<String> relatedElementId,
                               @JsonProperty(value = "expression") String expression,
                               @JsonProperty(value = "showImmediately") boolean showImmediately) {
        super(id);
        this.relatedElementId = ImmutableList.copyOf(relatedElementId);
        this.expression = expression;
        this.showImmediately = showImmediately;
    }


    public RelatedQuestionRule(@JsonProperty(value = "id") String id,
                               @JsonProperty(value = "relatedElementId") String relatedElementId,
                               @JsonProperty(value = "expression") String expression,
                               @JsonProperty(value = "showImmediately") boolean showImmediately) {
        super(id);
        this.relatedElementId = ImmutableList.of(relatedElementId);
        this.expression = expression;
        this.showImmediately = showImmediately;
    }

    public String getExpression() {
        return expression;
    }

    public List<String> getRelatedElementId() {
        return relatedElementId;
    }


    public boolean getShowImmediately() {
        return showImmediately;
    }

    @Override
    public List<Element> getChildren(final Map<String, String> values) {
        for (String relatedElemId : relatedElementId) {
            final String value = values.get(relatedElemId);
            if ((value == null && showImmediately) || RegexRule.evaluate(value, expression)) {
                return this.children;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Element addChild(Element... children) {
        if (this.children.isEmpty()) {
            this.children.add(new Group(this.id + "Group"));
        }
        this.children.get(0).addChild(children);
        return this;
    }
}
