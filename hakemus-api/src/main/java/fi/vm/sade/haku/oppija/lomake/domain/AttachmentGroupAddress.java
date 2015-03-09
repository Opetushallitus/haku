package fi.vm.sade.haku.oppija.lomake.domain;

import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.SimpleAddress;

import java.util.Date;

public class AttachmentGroupAddress {
    private final boolean useFirstAoAddress;
    private final Date deliveryDue;
    private final SimpleAddress deliveryAddress;

    public AttachmentGroupAddress(boolean useFirstAoAddress, Date deliveryDue, SimpleAddress deliveryAddress) {
        this.useFirstAoAddress = useFirstAoAddress;
        this.deliveryDue = deliveryDue;
        this.deliveryAddress = deliveryAddress;
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
}
