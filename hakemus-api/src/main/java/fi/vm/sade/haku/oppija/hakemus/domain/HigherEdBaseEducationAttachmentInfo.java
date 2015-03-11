package fi.vm.sade.haku.oppija.hakemus.domain;

import java.util.Date;

public class HigherEdBaseEducationAttachmentInfo {

    public final Address address;
    public final OriginatorType originatorType;
    public final String originatorId;
    public final Date deadline;

    public enum OriginatorType {
        applicationOption,
        group
    }

    public HigherEdBaseEducationAttachmentInfo(Address address, OriginatorType originatorType, String originatorId, Date deadline) {
        this.address = address;
        this.originatorType = originatorType;
        this.originatorId = originatorId;
        this.deadline = deadline;
    }
}
