package fi.vm.sade.haku.oppija.hakemus.domain;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import fi.vm.sade.koulutusinformaatio.domain.dto.AddressDTO;

public class HigherEdBaseEducationAttachmentInfo {

    public final I18nText attachmentName;
    public final String recipientName;
    public final AddressDTO addressDTO;
    public final String attachmentOriginatorAoId;
    public final String attachmentOriginatorGroupId;

    public enum OriginatorType {
        applicationOption,
        group
    }

    public HigherEdBaseEducationAttachmentInfo(I18nText attachmentName, String recipientName, AddressDTO addressDTO, OriginatorType originatorType, String originatorId) {
        this.attachmentName = attachmentName;
        this.recipientName = StringUtil.safeToString(recipientName);
        this.addressDTO = addressDTO;
        if (originatorType == OriginatorType.applicationOption) {
            this.attachmentOriginatorAoId = originatorId;
            this.attachmentOriginatorGroupId = null;
        }
        else {
            this.attachmentOriginatorGroupId = originatorId;
            this.attachmentOriginatorAoId = null;
        }
    }
}
