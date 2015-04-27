package fi.vm.sade.haku.oppija.lomake.domain;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;

import java.util.*;

public class ApplicationSystemBuilder {
    private String id;
    private Form form;
    private I18nText name;
    private String state;
    private List<ApplicationPeriod> applicationPeriods;
    private String applicationSystemType;
    private Boolean usePriorities;
    private String hakutapa;
    private Integer hakukausiVuosi;
    private String hakukausiUri;
    private List<Element> applicationCompleteElements;
    private List<Element> additionalPrintElements;
    private List<ApplicationOptionAttachmentRequest> applicationOptionAttachmentRequests;
    private List<AttachmentGroupAddress> attachmentGroupAddresses;
    private int maxApplicationOptions;
    private String kohdejoukkoUri;
    private List<String> allowedLanguages;
    private List<String> aosForAutomaticEligibility;
    private Date lastGenerated;

    public ApplicationSystemBuilder() {
        this.additionalPrintElements = new ArrayList<>();
        this.applicationCompleteElements = new ArrayList<>();
        this.applicationOptionAttachmentRequests = new ArrayList<>();
        this.attachmentGroupAddresses = new ArrayList<>();
    }

    public ApplicationSystemBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public ApplicationSystemBuilder setForm(Form form) {
        this.form = form;
        return this;
    }

    public ApplicationSystemBuilder setName(I18nText name) {
        this.name = name;
        return this;
    }

    public ApplicationSystemBuilder setApplicationPeriods(List<ApplicationPeriod> applicationPeriods) {
        this.applicationPeriods = applicationPeriods;
        return this;
    }

    public ApplicationSystemBuilder addAdditionalInformationElements(List<Element> elements) {
        this.additionalPrintElements.addAll(elements);
        return this;
    }


    public ApplicationSystemBuilder setApplicationSystemType(String applicationSystemType) {
        this.applicationSystemType = applicationSystemType;
        return this;
    }

    public ApplicationSystemBuilder setHakukausiVuosi(Integer hakukausiVuosi) {
        this.hakukausiVuosi = hakukausiVuosi;
        return this;
    }

    public ApplicationSystemBuilder setHakukausiUri(String hakukausiUri) {
        this.hakukausiUri = hakukausiUri;
        return this;
    }

    public ApplicationSystemBuilder addApplicationCompleteElements(List<Element> applicationCompleteElements) {
        this.applicationCompleteElements.addAll(applicationCompleteElements);
        return this;
    }

    public ApplicationSystemBuilder addApplicationOptionAttachmentRequests(List<ApplicationOptionAttachmentRequest> applicationOptionAttachmentRequests) {
        this.applicationOptionAttachmentRequests.addAll(applicationOptionAttachmentRequests);
        return this;
    }

    public ApplicationSystemBuilder addAttachmentGroupAddresses(List<AttachmentGroupAddress> attachmentGroupAddresses) {
        this.attachmentGroupAddresses.addAll(attachmentGroupAddresses);
        return this;
    }

    public ApplicationSystem get() {
        return new ApplicationSystem(id, form, name, state, applicationPeriods,
                applicationSystemType, usePriorities, hakutapa, hakukausiVuosi, hakukausiUri, kohdejoukkoUri, applicationCompleteElements,
                additionalPrintElements, applicationOptionAttachmentRequests, attachmentGroupAddresses, maxApplicationOptions, allowedLanguages,
                aosForAutomaticEligibility, lastGenerated);
    }

    public ApplicationSystemBuilder setMaxApplicationOptions(int maxHakukohdes) {
        this.maxApplicationOptions = maxHakukohdes;
        return this;
    }

    public ApplicationSystemBuilder setKohdejoukkoUri(String kohdejoukkoUri) {
        this.kohdejoukkoUri = kohdejoukkoUri;
        return this;
    }

    public ApplicationSystemBuilder setHakutapa(String hakutapa) {
        this.hakutapa = hakutapa;
        return this;
    }

    public ApplicationSystemBuilder setState(String tila) {
        this.state = tila;
        return this;
    }

    public ApplicationSystemBuilder setLastGenerated(Date lastGenerated) {
        this.lastGenerated = lastGenerated;
        return this;
    }

    public ApplicationSystemBuilder setUsePriorities(Boolean usePriorities) {
        this.usePriorities = usePriorities;
        return this;
    }

    public ApplicationSystemBuilder setAllowedLanguages(List<String> allowedLanguages) {
        this.allowedLanguages = allowedLanguages;
        return this;
    }

    public ApplicationSystemBuilder setAosForAutomaticEligibility(List<String> aosForAutomaticEligibility) {
        this.aosForAutomaticEligibility = aosForAutomaticEligibility;
        return this;
    }

}
