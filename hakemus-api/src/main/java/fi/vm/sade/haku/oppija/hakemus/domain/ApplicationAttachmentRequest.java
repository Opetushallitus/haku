package fi.vm.sade.haku.oppija.hakemus.domain;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ApplicationAttachmentRequest {

    public static enum ReceptionStatus {
        ARRIVED,
        ARRIVED_LATE,
        NOT_RECEIVED
    }

    public static enum ProcessingStatus{
        CHECKED,
        NOT_CHECKED,
        INADEQUATE,
        COMPLEMENT_REQUESTED
    }

    private final String aoId;
    private final String aoGroupId;
    private final ReceptionStatus receptionStatus;
    private final ProcessingStatus processingStatus;
    private final ApplicationAttachment applicationAttachment;

    @JsonCreator
    public ApplicationAttachmentRequest(@JsonProperty(value = "aoId") String aoId,
                                        @JsonProperty(value = "aoGroupId") String aoGroupId,
                                        @JsonProperty(value = "requestStatus") ApplicationAttachmentRequest.ReceptionStatus receptionStatus,
                                        @JsonProperty(value = "processingStatus") ApplicationAttachmentRequest.ProcessingStatus processingStatus,
                                        @JsonProperty(value = "applicationAttachment") ApplicationAttachment applicationAttachment) {
        this.aoId = aoId;
        this.aoGroupId = aoGroupId;
        this.receptionStatus = receptionStatus;
        this.processingStatus = processingStatus;
        this.applicationAttachment = applicationAttachment;
    }

    public String getAoId() {
        return aoId;
    }

    public String getAoGroupId() {
        return aoGroupId;
    }

    public ReceptionStatus getReceptionStatus() {
        return receptionStatus;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public ApplicationAttachment getApplicationAttachment() {
        return applicationAttachment;
    }
}
