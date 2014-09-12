package fi.vm.sade.haku.oppija.hakemus.domain;

import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAttachment;

public class ApplicationAttachmentRequestBuilder {
    private String aoId;
    private String aoGroupId;
    private ApplicationAttachmentRequest.Status requestStatus = ApplicationAttachmentRequest.Status.NOT_RECEIVED;
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

    public ApplicationAttachmentRequestBuilder setRequestStatus(ApplicationAttachmentRequest.Status requestStatus) {
        this.requestStatus = requestStatus;
        return this;
    }

    public ApplicationAttachmentRequestBuilder setApplicationAttachment(ApplicationAttachment applicationAttachment) {
        this.applicationAttachment = applicationAttachment;
        return this;
    }

    public ApplicationAttachmentRequest build() {
        return new ApplicationAttachmentRequest(aoId, aoGroupId, requestStatus, applicationAttachment);
    }
}