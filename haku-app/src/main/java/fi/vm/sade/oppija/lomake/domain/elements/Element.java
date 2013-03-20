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


import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.lomake.domain.Attribute;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.custom.*;
import fi.vm.sade.oppija.lomake.domain.elements.questions.*;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.domain.rules.AddElementRule;
import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomake.validation.Validator;
import fi.vm.sade.oppija.lomake.validation.validators.ContainedInOtherFieldValidator;
import fi.vm.sade.oppija.lomake.validation.validators.ISO88591NameValidator;
import fi.vm.sade.oppija.lomake.validation.validators.RegexFieldFieldValidator;
import fi.vm.sade.oppija.lomake.validation.validators.RequiredFieldFieldValidator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

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
                @JsonSubTypes.Type(value = Group.class),
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
                @JsonSubTypes.Type(value = Text.class),
                @JsonSubTypes.Type(value = WorkExperienceTheme.class),
                @JsonSubTypes.Type(value = Notification.class),
                @JsonSubTypes.Type(value = DateQuestion.class)
        }
)
public abstract class Element implements Serializable {
    private static final long serialVersionUID = 3485937447100055723L;
    public static final String ID_DELIMITER = "_";
    protected final String id;
    protected transient String type = this.getClass().getSimpleName();

    protected I18nText help;

    protected final transient List<Validator> validators = new ArrayList<Validator>();

    protected final List<Element> children = new ArrayList<Element>();


    protected Map<String, Attribute> attributes = new HashMap<String, Attribute>();


    protected Element(@JsonProperty String id) {
        this.id = id;
        addAttribute("id", id);
        this.help = null;
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public String getType() {
        return type;
    }


    @JsonDeserialize(keyAs = String.class, contentAs = Attribute.class)
    @JsonSerialize(keyAs = String.class, contentAs = Attribute.class)
    public Map<String, Attribute> getAttributes() {
        return attributes;
    }

    @JsonDeserialize(keyAs = String.class, contentAs = Attribute.class)
    @JsonSerialize(keyAs = String.class, contentAs = Attribute.class)
    public void setAttributes(final Map<String, Attribute> attributes) {
        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
            addAttribute(entry.getValue().getKey(), entry.getValue().getValue());
        }
    }

    public I18nText getHelp() {
        return help;
    }

    public void setHelp(final I18nText help) {
        this.help = help;
    }

    public Element addChild(Element child) {
        this.children.add(child);
        return this;
    }

    public void addAttribute(final String key, final String value) {
        checkNotNull(key, "Attribute's key cannot be null");
        checkNotNull(value, "Attribute's value cannot be null");
        this.attributes.put(key, new Attribute(key, value));
        if (key.equals("required")) {
            addValidator(new RequiredFieldFieldValidator(this.id));
        } else if (key.equals("pattern")) {
            addValidator(new RegexFieldFieldValidator(this.id, value));
        } else if (key.equals("containedInOther")) {
            addValidator(new ContainedInOtherFieldValidator(this.id, value));
        } else if (key.equals("iso8859name")) {
            addValidator(new ISO88591NameValidator(this.id));
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

        return Objects.equal(this.id, element.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @JsonIgnore
    public List<Validator> getValidators() {
        return ImmutableList.copyOf(validators);
    }

    public void addValidator(final Validator validator) {
        this.validators.add(validator);
    }

    public List<Element> getChildren(Map<String, String> values) {
        return getChildren();
    }

    public List<Element> getChildren() {
        return ImmutableList.copyOf(children);
    }

    @JsonIgnore
    public Element getChildById(final String id) {
        Element element = getChildById(this, id);
        if (element == null) {
            throw new ResourceNotFoundExceptionRuntime("Could not find element " + id);
        }

        return element;
    }

    @JsonIgnore
    protected Element getChildById(final Element element, final String id) {
        if (element.getId().equals(id)) {
            return element;
        }
        Element tmp = null;
        for (Element child : element.getChildren()) {
            tmp = getChildById(child, id);
            if (tmp != null) {
                return tmp;
            }
        }
        return tmp;
    }

    public final boolean hasChildren() {
        return this.children != null && this.children.size() > 0;
    }
}
