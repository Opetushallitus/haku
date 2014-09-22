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
        COMPLEMENT_REQUESTED,
        UNNECESSARY
    }

    private final String id;
    private final String preferenceAoId;
    private final String preferenceAoGroupId;
    private final ReceptionStatus receptionStatus;
    private final ProcessingStatus processingStatus;
    private final ApplicationAttachment applicationAttachment;

    @JsonCreator
    public ApplicationAttachmentRequest(@JsonProperty(value = "id") final String id,
                                        @JsonProperty(value = "preferenceAoId") final String preferenceAoId,
                                        @JsonProperty(value = "preferenceAoGroupId") final String preferenceAoGroupId,
                                        @JsonProperty(value = "requestStatus") final ApplicationAttachmentRequest.ReceptionStatus receptionStatus,
                                        @JsonProperty(value = "processingStatus") final ApplicationAttachmentRequest.ProcessingStatus processingStatus,
                                        @JsonProperty(value = "applicationAttachment") final ApplicationAttachment applicationAttachment) {
        this.id = id;
        this.preferenceAoId = preferenceAoId;
        this.preferenceAoGroupId = preferenceAoGroupId;
        this.receptionStatus = receptionStatus;
        this.processingStatus = processingStatus;
        this.applicationAttachment = applicationAttachment;
    }

    public String getId(){
        return id;
    }

    public String getAoId() {
        return preferenceAoId;
    }

    public String getAoGroupId() {
        return preferenceAoGroupId;
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
