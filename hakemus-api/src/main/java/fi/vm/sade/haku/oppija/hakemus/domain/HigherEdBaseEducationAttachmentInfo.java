package fi.vm.sade.haku.oppija.hakemus.domain;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;

import java.util.Date;

public class HigherEdBaseEducationAttachmentInfo {

    public final Address address;
    public final OriginatorType originatorType;
    public final String originatorId;
    public final String providerId;
    public final I18nText description;
    public final Date deadline;
    public final I18nText helpText;

    public enum OriginatorType {
        applicationOption,
        group
    }

    public HigherEdBaseEducationAttachmentInfo(Address address, OriginatorType originatorType, String originatorId, String providerId, I18nText description, Date deadline, I18nText helpText) {
        this.address = address;
        this.originatorType = originatorType;
        this.originatorId = originatorId;
        this.providerId = providerId;
        this.description = description;
        this.deadline = deadline;
        this.helpText = helpText;
    }
}
