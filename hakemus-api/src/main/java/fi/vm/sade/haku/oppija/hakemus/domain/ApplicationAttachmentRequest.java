package fi.vm.sade.haku.oppija.hakemus.domain;

import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAttachment;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ApplicationAttachmentRequest {

    public static enum Status {
        ARRIVED,
        ARRIVED_LATE,
        NOT_RECEIVED
    }

    private final String aoId;
    private final String aoGroupId;
    private final Status requestStatus;
    private final ApplicationAttachment applicationAttachment;

    @JsonCreator
    public ApplicationAttachmentRequest(@JsonProperty(value = "aoId") String aoId,
                                        @JsonProperty(value = "aoGroupId") String aoGroupId,
                                        @JsonProperty(value = "requestStatus") ApplicationAttachmentRequest.Status requestStatus,
                                        @JsonProperty(value = "applicationAttachment") ApplicationAttachment applicationAttachment) {
        this.aoId = aoId;
        this.aoGroupId = aoGroupId;
        this.requestStatus = requestStatus;
        this.applicationAttachment = applicationAttachment;
    }

    public String getAoId() {
        return aoId;
    }

    public String getAoGroupId() {
        return aoGroupId;
    }

    public Status getRequestStatus() {
        return requestStatus;
    }

    public ApplicationAttachment getApplicationAttachment() {
        return applicationAttachment;
    }
}
