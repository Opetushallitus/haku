package fi.vm.sade.haku.oppija.lomake.domain;

import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.SimpleAddress;

import java.util.Date;

public class AttachmentGroupAddress {
    private final String groupId;
    private final boolean useFirstAoAddress;
    private final Date deliveryDue;
    private final SimpleAddress deliveryAddress;
    private final I18nText helpText;

    public AttachmentGroupAddress(String groupId, boolean useFirstAoAddress, Date deliveryDue, SimpleAddress deliveryAddress, I18nText helpText) {
        this.groupId = groupId;
        this.useFirstAoAddress = useFirstAoAddress;
        this.deliveryDue = deliveryDue;
        this.deliveryAddress = deliveryAddress;
        this.helpText = helpText;
    }

    public String getGroupId() {
        return groupId;
    }

    public boolean isUseFirstAoAddress() {
        return useFirstAoAddress;
    }

    public Date getDeliveryDue() {
        return deliveryDue;
    }

    public SimpleAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public I18nText getHelpText() {
        return helpText;
    }
}
