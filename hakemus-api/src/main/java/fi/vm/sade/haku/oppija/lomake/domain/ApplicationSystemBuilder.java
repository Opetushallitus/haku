package fi.vm.sade.haku.oppija.lomake.domain;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private int maxApplicationOptions;
    private String kohdejoukkoUri;
    private List<String> allowedLanguages;
    private Date lastGenerated;

    public ApplicationSystemBuilder() {
        this.additionalPrintElements = new ArrayList<Element>();
        this.applicationOptionAttachmentRequests = new ArrayList<ApplicationOptionAttachmentRequest>();
    }

    public ApplicationSystemBuilder addId(String id) {
        this.id = id;
        return this;
    }

    public ApplicationSystemBuilder addForm(Form form) {
        this.form = form;
        return this;
    }

    public ApplicationSystemBuilder addName(I18nText name) {
        this.name = name;
        return this;
    }

    public ApplicationSystemBuilder addApplicationPeriods(List<ApplicationPeriod> applicationPeriods) {
        this.applicationPeriods = applicationPeriods;
        return this;
    }

    public ApplicationSystemBuilder addAdditionalInformationElements(List<Element> elements) {
        this.additionalPrintElements.addAll(elements);
        return this;
    }


    public ApplicationSystemBuilder addApplicationSystemType(String applicationSystemType) {
        this.applicationSystemType = applicationSystemType;
        return this;
    }

    public ApplicationSystemBuilder addHakukausiVuosi(Integer hakukausiVuosi) {
        this.hakukausiVuosi = hakukausiVuosi;
        return this;
    }

    public ApplicationSystemBuilder addHakukausiUri(String hakukausiUri) {
        this.hakukausiUri = hakukausiUri;
        return this;
    }

    public ApplicationSystemBuilder addApplicationCompleteElements(List<Element> applicationCompleteElements) {
        this.applicationCompleteElements = applicationCompleteElements;
        return this;
    }

    public ApplicationSystemBuilder addApplicationOptionAttachmentRequests(List<ApplicationOptionAttachmentRequest> applicationOptionAttachmentRequests) {
        this.applicationOptionAttachmentRequests = applicationOptionAttachmentRequests;
        return this;
    }

    public ApplicationSystem get() {
        return new ApplicationSystem(id, form, name, state, applicationPeriods,
                applicationSystemType, usePriorities, hakutapa, hakukausiVuosi, hakukausiUri, kohdejoukkoUri, applicationCompleteElements,
                additionalPrintElements, applicationOptionAttachmentRequests, maxApplicationOptions, allowedLanguages, lastGenerated);
    }

    public ApplicationSystemBuilder addMaxApplicationOptions(int maxHakukohdes) {
        this.maxApplicationOptions = maxHakukohdes;
        return this;
    }

    public ApplicationSystemBuilder addKohdejoukkoUri(String kohdejoukkoUri) {
        this.kohdejoukkoUri = kohdejoukkoUri;
        return this;
    }

    public ApplicationSystemBuilder addHakutapa(String hakutapa) {
        this.hakutapa = hakutapa;
        return this;
    }

    public ApplicationSystemBuilder addState(String tila) {
        this.state = tila;
        return this;
    }

    public ApplicationSystemBuilder addLastGenerated(Date lastGenerated) {
        this.lastGenerated = lastGenerated;
        return this;
    }

    public ApplicationSystemBuilder addUsePriorities(Boolean usePriorities) {
        this.usePriorities = usePriorities;
        return this;
    }

    public ApplicationSystemBuilder addAllowedLanguages(List<String> allowedLanguages) {
        this.allowedLanguages = allowedLanguages;
        return this;
    }
}
