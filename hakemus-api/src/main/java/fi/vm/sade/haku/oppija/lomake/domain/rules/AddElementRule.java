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

import com.google.common.base.Strings;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author jteuho
 */
public class AddElementRule extends Element {

    private static final long serialVersionUID = -911312576973189581L;
    private final String relatedElementId;
    private final I18nText text;

    public AddElementRule(@JsonProperty(value = "id") String id,
                          @JsonProperty(value = "relatedElementId") String relatedElementId,
                          @JsonProperty(value = "text") I18nText text) {
        super(id);
        this.text = text;
        this.relatedElementId = relatedElementId;
    }

    public I18nText getText() {
        return text;
    }

    public String getRelatedElementId() {
        return relatedElementId;
    }

    @Override
    public List<Element> getChildren(final Map<String, String> values) {
        List<Element> children = getChildren();
        for (Element child : children) {
            if (values.containsKey(child.getId()) && !Strings.isNullOrEmpty(values.get(child.getId()))) {
                return children;
            }
        }
        return Collections.emptyList();
    }

}
