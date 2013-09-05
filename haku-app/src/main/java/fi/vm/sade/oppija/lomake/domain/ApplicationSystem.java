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

package fi.vm.sade.oppija.lomake.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationSystem implements Serializable {

    private static final long serialVersionUID = 709005625385191180L;
    @Id
    @XmlAttribute
    private String id;
    @XmlAttribute
    private Form form;
    @XmlAttribute
    private I18nText name;
    @XmlAttribute
    private List<ApplicationPeriod> applicationPeriods;

    @PersistenceConstructor
    public ApplicationSystem(final String id, final Form form, final I18nText name,
                             final List<ApplicationPeriod> applicationPeriods) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(name);
        this.id = id;
        this.form = form;
        this.name = name;
        this.applicationPeriods = applicationPeriods != null ?
                ImmutableList.copyOf(applicationPeriods) : Lists.<ApplicationPeriod>newArrayList();
    }

    @Transient
    public boolean isActive() {
        for (ApplicationPeriod ap : applicationPeriods) {
            if (ap.isActive()) {
                return true;
            }
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public Form getForm() {
        return form;
    }

    public I18nText getName() {
        return name;
    }

    public List<ApplicationPeriod> getApplicationPeriods() {
        return applicationPeriods;
    }
}
