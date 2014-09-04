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

package fi.vm.sade.haku.oppija.lomake.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Document
public class ApplicationSystem implements Serializable {

    private static final long serialVersionUID = 709005625385191180L;
    @Id
    private String id;
    private Form form;
    private I18nText name;
    private List<ApplicationPeriod> applicationPeriods;
    private String applicationSystemType;
    private Integer hakukausiVuosi;
    private String hakukausiUri;
    private String kohdejoukkoUri;
    private List<Element> applicationCompleteElements;
    private List<Element> additionalInformationElements;
    private List<ApplicationOptionAttachmentRequest> applicationOptionAttachmentRequests;
    private int maxApplicationOptions;

    public ApplicationSystem(final String id, final Form form, final I18nText name,
                             final List<ApplicationPeriod> applicationPeriods,
                             final String applicationSystemType, Integer hakukausiVuosi,
                             final String hakukausiUri,
                             final String kohdejoukkoUri,
                             final List<Element> applicationCompleteElements,
                             final List<Element> additionalInformationElements,
                             final List<ApplicationOptionAttachmentRequest> applicationOptionAttachmentRequests,
                             final Integer maxApplicationOptions) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(name);
        this.id = id;
        this.form = form;
        this.name = name;
        this.applicationPeriods = applicationPeriods != null ?
                ImmutableList.copyOf(applicationPeriods) : Lists.<ApplicationPeriod>newArrayList();
        this.applicationSystemType = applicationSystemType;
        this.hakukausiVuosi = hakukausiVuosi;
        this.hakukausiUri = hakukausiUri;
        this.kohdejoukkoUri = kohdejoukkoUri;
        this.applicationCompleteElements = applicationCompleteElements;
        this.additionalInformationElements = additionalInformationElements;
        this.applicationOptionAttachmentRequests = applicationOptionAttachmentRequests;
        this.maxApplicationOptions = maxApplicationOptions != null ?
                maxApplicationOptions.intValue() : 1;
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

    public String getApplicationSystemType() {
        return applicationSystemType;
    }

    public Integer getHakukausiVuosi() {
        return hakukausiVuosi;
    }

    public String getHakukausiUri() {
        return hakukausiUri;
    }

    public String getKohdejoukkoUri() {
        return kohdejoukkoUri;
    }

    public List<Element> getApplicationCompleteElements() {
        return applicationCompleteElements;
    }

    public List<Element> getAdditionalInformationElements() {
        return additionalInformationElements;
    }

    public List<ApplicationOptionAttachmentRequest> getApplicationOptionAttachmentRequests() {
        return applicationOptionAttachmentRequests;
    }

    public int getMaxApplicationOptions() {
        return maxApplicationOptions;
    }
}
