package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;

import java.util.Date;

public class ApplicationAttachmentBuilder {

    private I18nText name;
    private I18nText header;
    private I18nText description;
    private Date deadline;
    private Address address;

    public static ApplicationAttachmentBuilder start() {
        return new ApplicationAttachmentBuilder();
    }

    public I18nText getName() {
        return name;
    }

    public ApplicationAttachmentBuilder setName(I18nText name) {
        this.name = name;
        return this;
    }

    public I18nText getHeader() {
        return header;
    }

    public ApplicationAttachmentBuilder setHeader(I18nText header) {
        this.header = header;
        return this;
    }

    public I18nText getDescription() {
        return description;
    }

    public ApplicationAttachmentBuilder setDescription(I18nText description) {
        this.description = description;
        return this;
    }

    public Date getDeadline() {
        return deadline;
    }

    public ApplicationAttachmentBuilder setDeadline(Date deadline) {
        this.deadline = deadline;
        return this;
    }

    public Address getAddress() {
        return address;
    }

    public ApplicationAttachmentBuilder setAddress(Address address) {
        this.address = address;
        return this;
    }

    public ApplicationAttachment build() {
        ApplicationAttachment attachment = new ApplicationAttachment();
        attachment.setAddress(address);
        attachment.setDeadline(deadline);
        attachment.setDescription(description);
        attachment.setHeader(header);
        attachment.setName(name);
        return attachment;
    }


}
