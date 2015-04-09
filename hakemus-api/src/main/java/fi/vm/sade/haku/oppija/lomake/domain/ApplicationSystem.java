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
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.*;

@Document
public class ApplicationSystem implements Serializable {

    private static final long serialVersionUID = 709005625385191180L;
    @Id
    private String id;
    private Form form;
    private I18nText name;
    private String state;
    private List<ApplicationPeriod> applicationPeriods;
    private String applicationSystemType;
    private String hakutapa;
    private Integer hakukausiVuosi;
    private String hakukausiUri;
    private String kohdejoukkoUri;
    private Date lastGenerated;
    private Boolean usePriorities;

    private List<Element> applicationCompleteElements;
    private List<Element> additionalInformationElements;
    private List<ApplicationOptionAttachmentRequest> applicationOptionAttachmentRequests;
    private List<AttachmentGroupAddress> attachmentGroupAddresses;
    private int maxApplicationOptions;
    private List<String> allowedLanguages;

    public ApplicationSystem(final String id, final Form form, final I18nText name, final String state,
                             final List<ApplicationPeriod> applicationPeriods,
                             final String applicationSystemType,
                             final Boolean usePriorities,
                             final String hakutapa,
                             final Integer hakukausiVuosi,
                             final String hakukausiUri,
                             final String kohdejoukkoUri,
                             final List<Element> applicationCompleteElements,
                             final List<Element> additionalInformationElements,
                             final List<ApplicationOptionAttachmentRequest> applicationOptionAttachmentRequests,
                             final List<AttachmentGroupAddress> attachmentGroupAddresses,
                             final Integer maxApplicationOptions,
                             final List<String> allowedLanguages,
                             final Date lastGenerated) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(name);
        this.id = id;
        this.form = form;
        this.name = name;
        this.state = state;
        if (applicationPeriods != null)
            this.applicationPeriods=ImmutableList.copyOf(applicationPeriods);
        else
            this.applicationPeriods=ImmutableList.of();
        this.applicationSystemType = applicationSystemType;
        this.usePriorities = usePriorities;
        this.hakutapa = hakutapa;
        this.hakukausiVuosi = hakukausiVuosi;
        this.hakukausiUri = hakukausiUri;
        this.kohdejoukkoUri = kohdejoukkoUri;
        this.applicationCompleteElements = applicationCompleteElements;
        this.additionalInformationElements = additionalInformationElements;
        this.applicationOptionAttachmentRequests = applicationOptionAttachmentRequests;
        this.attachmentGroupAddresses = attachmentGroupAddresses;
        this.maxApplicationOptions = maxApplicationOptions != null ?
                maxApplicationOptions.intValue() : 1;
        this.allowedLanguages = allowedLanguages;
        this.lastGenerated = lastGenerated;
    }

    @Transient
    public boolean isActive() {
        if (!isPublished()) {
            return false;
        }
        for (ApplicationPeriod ap : applicationPeriods) {
            if (ap.isActive()) {
                return true;
            }
        }
        return false;
    }

    @Transient
    public boolean isPublished() {
        return "JULKAISTU".equals(state);
    }

    @Transient
    public boolean baseEducationDoesNotRestrictApplicationOptions() {
        return OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(getKohdejoukkoUri()) ||
                OppijaConstants.KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN.equals(getKohdejoukkoUri());
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

    public String getState() {
        return state;
    }

    public List<ApplicationPeriod> getApplicationPeriods() {
        return applicationPeriods;
    }

    public String getApplicationSystemType() {
        return applicationSystemType;
    }

    public boolean isUsePriorities() {
        return usePriorities != null && usePriorities;
    }

    public String getHakutapa() {
        return this.hakutapa;
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

    public List<AttachmentGroupAddress> getAttachmentGroupAddresses() {
        if(attachmentGroupAddresses == null) {
            return new ArrayList<>();
        }
        return attachmentGroupAddresses;
    }

    public Date getLastGenerated() {
        return lastGenerated;
    }

    public int getMaxApplicationOptions() {
        return maxApplicationOptions;
    }

    public List<String> getAllowedLanguages() {
        return allowedLanguages;
    }

}
