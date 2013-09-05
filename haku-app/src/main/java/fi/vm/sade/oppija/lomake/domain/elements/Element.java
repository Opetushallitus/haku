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
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.custom.Popup;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.validation.Validator;
import org.springframework.data.annotation.Transient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Element implements Serializable {
    private static final long serialVersionUID = 3485937447100055723L;
    public static final String ID_DELIMITER = "_";

    @Transient
    protected final String type = this.getClass().getSimpleName();

    protected final String id;
    protected final List<Validator> validators;
    protected final List<Element> children;
    protected final Map<String, String> attributes;
    protected I18nText help;
    protected Element popup;


    protected Element(final String id) {
        this.id = id;
        this.help = null;
        this.validators = new ArrayList<Validator>();
        this.children = new ArrayList<Element>();
        this.attributes = new HashMap<String, String>();
        addAttribute("id", id);

    }

    public String getId() {
        return id;
    }

    @Transient
    public String getType() {
        return type;
    }

    public Map<String, String> getAttributes() {
        return ImmutableMap.copyOf(attributes);
    }

    public I18nText getHelp() {
        return help;
    }

    public void setHelp(final I18nText help) {
        this.help = help;
    }

    public Element getPopup() {
        return this.popup;
    }

    public void setPopup(Popup popup) {
        this.popup = popup;
    }

    public Element addChild(Element... children) {
        Collections.addAll(this.children, children);
        return this;
    }

    public void addAttribute(final String key, final String value) {
        checkNotNull(key, "Attribute's key cannot be null");
        checkNotNull(value, "Attribute's value cannot be null");
        if (!attributes.containsKey(key)) {
            this.attributes.put(key, value);
        }
    }


    @Transient
    public final String getAttributeString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> attr : attributes.entrySet()) {
            if (!"required".equals(attr.getKey())) {
                builder.append(attr.getKey());
                builder.append("=\"");
                builder.append(attr.getValue());
                builder.append("\" ");
            }
        }
        return builder.toString();
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

    public List<Validator> getValidators() {
        return ImmutableList.copyOf(validators);
    }

    public void setValidators(final List<Validator> validators) {
        this.validators.addAll(validators);
    }

    public void setValidator(final Validator validator) {
        this.validators.add(validator);
    }

    public List<Element> getChildren(Map<String, String> values) {
        return getChildren();
    }

    public List<Element> getChildren() {
        return ImmutableList.copyOf(children);
    }

    public static List<Element> getAllChildren(Element element) {
        return element.getAllChildren();
    }

    private List<Element> getAllChildren() {
        ArrayList<Element> allChildren = new ArrayList<Element>();
        for (Element child : children) {
            allChildren.add(child);
            allChildren.addAll(child.getAllChildren());
        }
        return allChildren;
    }


    @Transient
    public Element getChildById(final String id) {
        Element element = getChildById(this, id);
        if (element == null) {
            throw new ResourceNotFoundExceptionRuntime("Could not find element " + id);
        }
        return element;
    }

    @Transient
    private Element getChildById(final Element element, final String id) {
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
        return this.children.size() > 0;
    }
}
