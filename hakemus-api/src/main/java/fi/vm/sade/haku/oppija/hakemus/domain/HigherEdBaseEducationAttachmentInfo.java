package fi.vm.sade.haku.oppija.hakemus.domain;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;

import java.util.Date;

public class HigherEdBaseEducationAttachmentInfo {

    public final I18nText attachmentName;
    public final Address address;
    public final OriginatorType originatorType;
    public final String originatorId;
    public final Date deadline;

    public enum OriginatorType {
        applicationOption,
        group
    }

    public HigherEdBaseEducationAttachmentInfo(I18nText attachmentName, Address address, OriginatorType originatorType, String originatorId, Date deadline) {
        this.attachmentName = attachmentName;
        this.address = address;
        this.originatorType = originatorType;
        this.originatorId = originatorId;
        this.deadline = deadline;
    }
}
