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

package fi.vm.sade.oppija.lomake.domain.elements;


import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.lomake.domain.Attribute;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.custom.*;
import fi.vm.sade.oppija.lomake.domain.elements.questions.*;
import fi.vm.sade.oppija.lomake.domain.rules.AddElementRule;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomake.validation.Validator;
import fi.vm.sade.oppija.lomake.validation.validators.ContainedInOtherFieldValidator;
import fi.vm.sade.oppija.lomake.validation.validators.RegexFieldFieldValidator;
import fi.vm.sade.oppija.lomake.validation.validators.RequiredFieldFieldValidator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

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
                @JsonSubTypes.Type(value = Option.class),
                @JsonSubTypes.Type(value = Radio.class),
                @JsonSubTypes.Type(value = TextArea.class),
                @JsonSubTypes.Type(value = Attachment.class),
                @JsonSubTypes.Type(value = Theme.class),
                @JsonSubTypes.Type(value = TextQuestion.class),
                @JsonSubTypes.Type(value = Phase.class),
                @JsonSubTypes.Type(value = RelatedQuestionRule.class),
                @JsonSubTypes.Type(value = AddElementRule.class),
                @JsonSubTypes.Type(value = GradeGrid.class),
                @JsonSubTypes.Type(value = SubjectRow.class),
                @JsonSubTypes.Type(value = LanguageRow.class),
                @JsonSubTypes.Type(value = CustomLanguageRow.class),
                @JsonSubTypes.Type(value = PreferenceTable.class),
                @JsonSubTypes.Type(value = PreferenceRow.class),
                @JsonSubTypes.Type(value = PostalCode.class),
                @JsonSubTypes.Type(value = SocialSecurityNumber.class),
                @JsonSubTypes.Type(value = Text.class)
        }
)
public abstract class Element {
    public static final String ID_DELIMITER = "_";
    protected final String id;
    protected transient String type = this.getClass().getSimpleName();
    protected String help = "";

    protected I18nText ihelp;

    protected final transient List<Validator> validators = new ArrayList<Validator>();

    protected final List<Element> children = new ArrayList<Element>();

    @JsonDeserialize(keyAs = String.class, contentAs = Attribute.class)
    @JsonSerialize(keyAs = String.class, contentAs = Attribute.class)
    protected final Map<String, Attribute> attributes = new HashMap<String, Attribute>();


    protected Element(@JsonProperty String id) {
        this.id = id;
        addAttribute("id", id);
        this.help = "";
        this.ihelp = new I18nText(id + "_help",
                ImmutableMap.of("fi", help, "sv", help + "_sv", "en", help + "_en"));
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
        this.ihelp = new I18nText(id + "_help",
                ImmutableMap.of("fi", help, "sv", help + "_sv", "en", help + "_en"));
    }

    public String getHelp() {
        return help;
    }

    public I18nText getIhelp() {
        return ihelp;
    }

    public void setIhelp(I18nText ihelp) {
        this.ihelp = ihelp;
    }

    public Element addChild(Element child) {
        this.children.add(child);
        return this;
    }

    public void addAttribute(final String key, final String value) {
        this.attributes.put(key, new Attribute(key, value));
    }


    public void init() {
        initValidators();
        for (Element child : children) {
            child.init();
        }
    }

    @JsonIgnore
    public String getAttributeString() {
        StringBuilder attrStr = new StringBuilder();
        for (Attribute attribute : attributes.values()) {
            if (!"required".equals(attribute.getKey())) {
                attrStr.append(attribute.getAsString());
            }

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

    @JsonIgnore
    public List<Validator> getValidators() {
        return validators;
    }

    public void initValidators() {
        for (Map.Entry<String, Attribute> attribute : attributes.entrySet()) {
            String key = attribute.getKey();
            String value = attribute.getValue().getValue();
            if (key.equals("required")) {
                this.validators.add(new RequiredFieldFieldValidator(this.id));
            } else if (key.equals("pattern")) {
                this.validators.add(new RegexFieldFieldValidator(this.id, value));
            } else if (key.equals("containedInOther")) {
                this.validators.add(new ContainedInOtherFieldValidator(this.id, value));
            }
        }
    }

    public List<Element> getChildren(Map<String, String> values) {
        return getChildren();
    }
}
