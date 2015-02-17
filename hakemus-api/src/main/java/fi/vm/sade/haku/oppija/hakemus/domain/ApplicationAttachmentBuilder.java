package fi.vm.sade.haku.oppija.hakemus.domain;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;

import java.util.Date;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.TranslationsUtil.ensureDefaultLanguageTranslations;

public class ApplicationAttachmentBuilder {

    private I18nText name;
    private I18nText header;
    private I18nText description;
    private Date deadline;
    private I18nText deliveryNote;
    private Address address;

    public static ApplicationAttachmentBuilder start() {
        return new ApplicationAttachmentBuilder();
    }

    public ApplicationAttachmentBuilder setName(I18nText name) {
        this.name = name;
        return this;
    }

    public ApplicationAttachmentBuilder setHeader(I18nText header) {
        this.header = header;
        return this;
    }

    public ApplicationAttachmentBuilder setDescription(I18nText description) {
        this.description = description;
        return this;
    }

    public ApplicationAttachmentBuilder setDeadline(Date deadline) {
        this.deadline = deadline;
        return this;
    }

    public ApplicationAttachmentBuilder setAddress(Address address) {
        this.address = address;
        return this;
    }

    public ApplicationAttachmentBuilder setDeliveryNote(I18nText deliveryNote) {
        this.deliveryNote = deliveryNote;
        return this;
    }

    public ApplicationAttachment build() {
        return new ApplicationAttachment(
                null != name ? ensureDefaultLanguageTranslations(name) : null,
                null != header ? ensureDefaultLanguageTranslations(header) : null,
                null != description ? ensureDefaultLanguageTranslations(description) : null,
                deadline,
                null != deliveryNote ? ensureDefaultLanguageTranslations(deliveryNote) : null,
                address);
    }


}
