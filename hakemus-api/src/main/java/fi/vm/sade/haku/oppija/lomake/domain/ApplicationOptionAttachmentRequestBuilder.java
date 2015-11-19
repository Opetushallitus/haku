package fi.vm.sade.haku.oppija.lomake.domain;

import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.SimpleAddress;

import java.util.Date;

public class ApplicationOptionAttachmentRequestBuilder {
    private Expr condition;
    private String applicationOptionId;
    private Boolean groupOption;
    private I18nText header;
    private I18nText description;
    private Date deliveryDue;
    private Boolean useGroupAddress;
    private Boolean useLopAddress;
    private SimpleAddress deliveryAddress;

    protected ApplicationOptionAttachmentRequestBuilder(){}

    public static ApplicationOptionAttachmentRequestBuilder start(){
        return new ApplicationOptionAttachmentRequestBuilder();
    }

    public ApplicationOptionAttachmentRequestBuilder setCondition(Expr condition) {
        this.condition = condition;
        return this;
    }

    public ApplicationOptionAttachmentRequestBuilder setApplicationOptionId(String applicationOptionId) {
        this.applicationOptionId = applicationOptionId;
        return this;
    }

    public ApplicationOptionAttachmentRequestBuilder setGroupOption(Boolean groupOption) {
        this.groupOption = groupOption;
        return this;
    }

    public ApplicationOptionAttachmentRequestBuilder setHeader(I18nText header) {
        this.header = header;
        return this;
    }

    public ApplicationOptionAttachmentRequestBuilder setDescription(I18nText description) {
        this.description = description;
        return this;
    }

    public ApplicationOptionAttachmentRequestBuilder setDeliveryDue(Date deliveryDue) {
        this.deliveryDue = deliveryDue;
        return this;
    }

    public ApplicationOptionAttachmentRequestBuilder setUseGroupAddress(Boolean useGroupAddress) {
        this.useGroupAddress = useGroupAddress;
        return this;
    }

    public ApplicationOptionAttachmentRequestBuilder setUseLopAddress(Boolean useLopAddress) {
        this.useLopAddress = useLopAddress;
        return this;
    }

    public ApplicationOptionAttachmentRequestBuilder setDeliveryAddress(SimpleAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public ApplicationOptionAttachmentRequest build() {
        return new ApplicationOptionAttachmentRequest(condition, applicationOptionId, groupOption, header, description, deliveryDue, useGroupAddress, deliveryAddress, useLopAddress);
    }
}