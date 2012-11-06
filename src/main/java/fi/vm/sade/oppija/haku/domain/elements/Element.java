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


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.vm.sade.oppija.haku.domain.Attribute;
import fi.vm.sade.oppija.haku.domain.elements.custom.*;
import fi.vm.sade.oppija.haku.domain.elements.questions.*;
import fi.vm.sade.oppija.haku.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.haku.domain.rules.SelectingSubmitRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jukka
 * @version 9/7/1210:29 AM}
 * @since 1.1
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = Attachment.class),
                @JsonSubTypes.Type(value = CheckBox.class),
                @JsonSubTypes.Type(value = DropdownSelect.class),
                @JsonSubTypes.Type(value = MultiSelect.class),
                @JsonSubTypes.Type(value = Option.class),
                @JsonSubTypes.Type(value = Radio.class),
                @JsonSubTypes.Type(value = TextArea.class),
                @JsonSubTypes.Type(value = Attachment.class),
                @JsonSubTypes.Type(value = Navigation.class),
                @JsonSubTypes.Type(value = Link.class),
                @JsonSubTypes.Type(value = Teema.class),
                @JsonSubTypes.Type(value = TextQuestion.class),
                @JsonSubTypes.Type(value = Vaihe.class),
                @JsonSubTypes.Type(value = RelatedQuestionRule.class),
                @JsonSubTypes.Type(value = SelectingSubmitRule.class),
                @JsonSubTypes.Type(value = GradeGrid.class),
                @JsonSubTypes.Type(value = SubjectRow.class),
                @JsonSubTypes.Type(value = LanguageRow.class),
                @JsonSubTypes.Type(value = CustomLanguageRow.class),
                @JsonSubTypes.Type(value = AddLanguageRow.class),
                @JsonSubTypes.Type(value = SortableTable.class),
                @JsonSubTypes.Type(value = PreferenceRow.class),
                @JsonSubTypes.Type(value = PostalCode.class)
        }
)
public abstract class Element {
    public static final String ID_DELIMITER = "_";
    final String id;
    transient String type = this.getClass().getSimpleName();
    String help;

    protected final List<Element> children = new ArrayList<Element>();

    @JsonDeserialize(keyAs = String.class, contentAs = Attribute.class)
    @JsonSerialize(keyAs = String.class, contentAs = Attribute.class)
    final Map<String, Attribute> attributes = new HashMap<String, Attribute>();


    protected Element(@JsonProperty String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public String getType() {
        return type;
    }

    public List<Element> getChildren() {
        return children;
    }

    @JsonDeserialize(keyAs = String.class, contentAs = Attribute.class)
    @JsonSerialize(keyAs = String.class, contentAs = Attribute.class)
    public Map<String, Attribute> getAttributes() {
        return attributes;
    }

    public void setHelp(final String help) {
        this.help = help;
    }

    public String getHelp() {
        return help;
    }

    public Element addChild(Element child) {
        this.children.add(child);
        return this;
    }

    public void addAttribute(final String key, final String value) {
        this.attributes.put(key, new Attribute(key, value));
    }

    @JsonIgnore
    public String getAttributeString() {
        StringBuilder attrStr = new StringBuilder();
        for (Attribute attribute : attributes.values()) {
            attrStr.append(attribute.getAsString());

        }
        return attrStr.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Element element = (Element) o;

        if (id != null ? !id.equals(element.id) : element.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
