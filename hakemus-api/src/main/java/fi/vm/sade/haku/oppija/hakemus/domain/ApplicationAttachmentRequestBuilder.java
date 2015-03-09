package fi.vm.sade.haku.oppija.hakemus.domain;

import java.util.UUID;

public class ApplicationAttachmentRequestBuilder {

    private String id;
    private String preferenceAoId;
    private String preferenceAoGroupId;
    private ApplicationAttachmentRequest.ReceptionStatus receptionStatus = ApplicationAttachmentRequest.ReceptionStatus.NOT_RECEIVED;
    private ApplicationAttachmentRequest.ProcessingStatus processingStatus = ApplicationAttachmentRequest.ProcessingStatus.NOT_CHECKED;

    private ApplicationAttachment applicationAttachment;

    public static ApplicationAttachmentRequestBuilder start(){
        return new ApplicationAttachmentRequestBuilder();
    }

    public ApplicationAttachmentRequestBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public ApplicationAttachmentRequestBuilder setPreferenceAoId(final String preferenceAoId) {
        this.preferenceAoId = preferenceAoId;
        return this;
    }

    public ApplicationAttachmentRequestBuilder setPreferenceAoGroupId(final String preferenceAoGroupId) {
        this.preferenceAoGroupId = preferenceAoGroupId;
        return this;
    }

    public ApplicationAttachmentRequestBuilder setReceptionStatus(final ApplicationAttachmentRequest.ReceptionStatus receptionStatus) {
        this.receptionStatus = receptionStatus;
        return this;
    }

    public ApplicationAttachmentRequestBuilder setProcessingStatus(final ApplicationAttachmentRequest.ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
        return this;
    }

    public ApplicationAttachmentRequestBuilder setApplicationAttachment(final ApplicationAttachment applicationAttachment) {
        this.applicationAttachment = applicationAttachment;
        return this;
    }

    public ApplicationAttachmentRequest build() {
        return new ApplicationAttachmentRequest(null == id? UUID.randomUUID().toString(): id , preferenceAoId, preferenceAoGroupId, receptionStatus, processingStatus, applicationAttachment);
    }
}