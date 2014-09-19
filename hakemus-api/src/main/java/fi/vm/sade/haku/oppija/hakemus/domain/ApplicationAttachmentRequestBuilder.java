package fi.vm.sade.haku.oppija.hakemus.domain;

public class ApplicationAttachmentRequestBuilder {
    private String aoId;
    private String aoGroupId;
    private ApplicationAttachmentRequest.ReceptionStatus receptionStatus = ApplicationAttachmentRequest.ReceptionStatus.NOT_RECEIVED;
    private ApplicationAttachmentRequest.ProcessingStatus processingStatus = ApplicationAttachmentRequest.ProcessingStatus.NOT_CHECKED;

    private ApplicationAttachment applicationAttachment;

    public static ApplicationAttachmentRequestBuilder start(){
        return new ApplicationAttachmentRequestBuilder();
    }

    public ApplicationAttachmentRequestBuilder setAoId(String aoId) {
        this.aoId = aoId;
        return this;
    }

    public ApplicationAttachmentRequestBuilder setAoGroupId(String aoGroupId) {
        this.aoGroupId = aoGroupId;
        return this;
    }

    public ApplicationAttachmentRequestBuilder setReceptionStatus(ApplicationAttachmentRequest.ReceptionStatus receptionStatus) {
        this.receptionStatus = receptionStatus;
        return this;
    }

    public ApplicationAttachmentRequestBuilder setProcessingStatus(ApplicationAttachmentRequest.ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
        return this;
    }

    public ApplicationAttachmentRequestBuilder setApplicationAttachment(ApplicationAttachment applicationAttachment) {
        this.applicationAttachment = applicationAttachment;
        return this;
    }

    public ApplicationAttachmentRequest build() {
        return new ApplicationAttachmentRequest(aoId, aoGroupId, receptionStatus, processingStatus, applicationAttachment);
    }
}