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

package fi.vm.sade.haku.oppija.lomake.domain.elements;


import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.Popup;
import fi.vm.sade.haku.oppija.lomake.validation.Validator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.I18nBundle;
import org.springframework.data.annotation.Transient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Element implements Serializable {
    private static final long serialVersionUID = 3485937447100055723L;

    @Transient
    protected final String type = this.getClass().getSimpleName();

    protected final String id;
    protected final List<Validator> validators;
    protected final List<Element> children;
    protected final Map<String, String> attributes;
    protected I18nText help;
    protected Element popup;
    private boolean inline;


    protected Element(final String id) {
        this.id = id;
        this.help = null;
        this.validators = new ArrayList<Validator>();
        this.children = new ArrayList<Element>();
        this.attributes = new HashMap<String, String>();
    }

    public String getId() {
        return id;
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
        } else
            throw new UnsupportedOperationException("Attribute \"" + key + "\" already set");
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

    public boolean isInline() {
        return inline;
    }

    public void setInline(boolean inline) {
        this.inline = inline;
    }

    /*
     * Required for fi.vm.sade.haku.oppija.lomake.domain.rule.RelatedQuestionRule to work
     */
    public List<Element> getChildren(Map<String, String> values) {
        return getChildren();
    }

    public List<Element> getChildren() {
        return ImmutableList.copyOf(children);
    }

    public List<Element> getAllChildren(Map<String, String> values) {
        List<Element> children = new ArrayList<Element>();
        for (Element child : getChildren(values)) {
            children.add(child);
            children.addAll(child.getAllChildren(values));
        }
        return children;
    }

    @Transient
    public String getType() {
        return type;
    }

    @Transient
    public final boolean hasChildren() {
        return this.children.size() > 0;
    }

    @Transient
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
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

    @Transient
    public final String getAttributeString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> attr : attributes.entrySet()) {
            builder.append(attr.getKey());
            builder.append("=\"");
            builder.append(attr.getValue());
            builder.append("\" ");
        }
        return builder.toString();
    }

    @Transient
    public Element[] getExtraExcelColumns(final I18nBundle i18nBundle) {
        return null;
    }

