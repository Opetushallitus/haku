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
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.*;

import static org.apache.commons.lang.BooleanUtils.toBoolean;

@Document
public class ApplicationSystem implements Serializable {

    public enum State {
        ACTIVE, LOCKED, PUBLISHED, CLOSED, ERROR
    }

    private static final long serialVersionUID = 709005625385191180L;
    @Id
    private String id;

    /**
     * When persisted by MongoTemplate, this field is de/compressed by hooks.
     * @see {@link ApplicationSystemMongoEventListener}.
     */
    private Form form;

    private I18nText name;
    private String state;
    private List<ApplicationPeriod> applicationPeriods;
    private String applicationSystemType;
    private String hakutapa;
    private Integer hakukausiVuosi;
    private String hakukausiUri;
    private String kohdejoukkoUri;
    private String kohdejoukonTarkenne;
    private Date lastGenerated;
    private Boolean usePriorities;
    private Boolean maksumuuriKaytossa;
    private Boolean automaticEligibilityInUse;

    private List<Element> applicationCompleteElements;
    private List<Element> additionalInformationElements;
    private List<ApplicationOptionAttachmentRequest> applicationOptionAttachmentRequests;
    private List<AttachmentGroupAddress> attachmentGroupAddresses;
    private int maxApplicationOptions;
    private List<String> allowedLanguages;
    private List<String> aosForAutomaticEligibility;

    public ApplicationSystem(final String id, final Form form, final I18nText name, final String state,
                             final List<ApplicationPeriod> applicationPeriods,
                             final String applicationSystemType,
                             final Boolean usePriorities,
                             final String hakutapa,
                             final Integer hakukausiVuosi,
                             final String hakukausiUri,
                             final String kohdejoukkoUri,
                             final String kohdejoukonTarkenne,
                             final List<Element> applicationCompleteElements,
                             final List<Element> additionalInformationElements,
                             final List<ApplicationOptionAttachmentRequest> applicationOptionAttachmentRequests,
                             final List<AttachmentGroupAddress> attachmentGroupAddresses,
                             final Integer maxApplicationOptions,
                             final List<String> allowedLanguages,
                             final Boolean automaticEligibilityInUse,
                             final List<String> aosForAutomaticEligibility,
                             final Date lastGenerated,
                             final Boolean maksumuuriKaytossa) {
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
        this.kohdejoukonTarkenne = kohdejoukonTarkenne;
        this.applicationCompleteElements = applicationCompleteElements;
        this.additionalInformationElements = additionalInformationElements;
        this.applicationOptionAttachmentRequests = applicationOptionAttachmentRequests;
        this.attachmentGroupAddresses = attachmentGroupAddresses;
        this.maxApplicationOptions = maxApplicationOptions != null ?
                maxApplicationOptions.intValue() : 1;
        this.allowedLanguages = allowedLanguages;
        this.automaticEligibilityInUse = automaticEligibilityInUse;
        this.aosForAutomaticEligibility = aosForAutomaticEligibility;
        this.lastGenerated = lastGenerated;
        this.maksumuuriKaytossa = maksumuuriKaytossa;
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
    public State getApplicationSystemState() {
        if (applicationPeriods.size() < 1 ){
            return State.ERROR;
        }
        for(ApplicationPeriod applicationPeriod: applicationPeriods) {
            if(applicationPeriod.isActive()) {
                return State.ACTIVE;
            }
        }
        final Date lastApplicationPeriodEnd = getLastApplicationPeriodEnd(applicationPeriods);
        final Date now = new Date();
        if (now.after(lastApplicationPeriodEnd)){
            return State.CLOSED;
        }
        for(ApplicationPeriod applicationPeriod: applicationPeriods) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(applicationPeriod.getStart());
            calendar.roll(Calendar.DATE, -2);
            if (now.after(calendar.getTime()) && now.before(applicationPeriod.getStart())) {
                return State.LOCKED;
            }
        }
        return State.PUBLISHED;
    }

    private Date getLastApplicationPeriodEnd(List<ApplicationPeriod> applicationPeriods) {
        SortedSet<Date> sort = new TreeSet<Date>();
        for(ApplicationPeriod applicationPeriod: applicationPeriods) {
            sort.add(applicationPeriod.getEnd());
        }
        return sort.last();
    }

    @Transient
    public boolean isPublished() {
        return "JULKAISTU".equals(state);
    }

    @Transient
    public boolean baseEducationDoesNotRestrictApplicationOptions() {
        return OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(getKohdejoukkoUri()) ||
                OppijaConstants.KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN.equals(getKohdejoukkoUri()) ||
                OppijaConstants.KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA.equals(getKohdejoukkoUri());
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

    public boolean isAutomaticEligibilityInUse() {
        return automaticEligibilityInUse != null && automaticEligibilityInUse;
    }

    public List<String> getAosForAutomaticEligibility() {
        return aosForAutomaticEligibility;
    }

    public boolean isMaksumuuriKaytossa() {
        // Applications prior to paywall may not have var set, make null-safe
        return toBoolean(maksumuuriKaytossa);
    }

    public String getKohdejoukonTarkenne() {
        return kohdejoukonTarkenne;
    }

    @JsonIgnore
    public boolean isHigherEducation() {
        return OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(kohdejoukkoUri);
    }
}
