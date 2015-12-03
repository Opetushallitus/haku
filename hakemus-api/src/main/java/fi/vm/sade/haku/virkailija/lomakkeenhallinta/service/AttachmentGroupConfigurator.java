package fi.vm.sade.haku.virkailija.lomakkeenhallinta.service;

import fi.vm.sade.haku.oppija.lomake.domain.AttachmentGroupAddress;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.GroupConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.SimpleAddress;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AttachmentGroupConfigurator {
    private final FormConfiguration formConfiguration;

    public AttachmentGroupConfigurator(FormConfiguration formConfiguration) {
        this.formConfiguration = formConfiguration;
    }

    public List<AttachmentGroupAddress> configureAttachmentGroupAddresses() {
        List<AttachmentGroupAddress> addresses = new ArrayList<>();
        for (GroupConfiguration groupConfiguration : formConfiguration.getGroupConfigurations()){
            switch (groupConfiguration.getType()) {
                case hakukohde_liiteosoite:
                    addresses.add(createLiiteOsoite(groupConfiguration));
                    break;
            }
        }
        return addresses;

    }

    private AttachmentGroupAddress createLiiteOsoite(GroupConfiguration configuration) {
        return new AttachmentGroupAddress(
                configuration.getGroupId(),
                parseUseFirstAoAddress(configuration.getConfigurations()),
                parseDeliveryDue(configuration.getConfigurations()),
                parseDeliveryAddress(configuration.getConfigurations()),
                configuration.getConfigurations() != null ? configuration.getConfigurations().helpText : null
        );
    }

    private boolean parseUseFirstAoAddress(GroupConfiguration.Configuration configs) {
        if(null != configs && null != configs.useFirstAoAddress) {
            return Boolean.valueOf(configs.useFirstAoAddress);
        }
        return true;
    }

    private Date parseDeliveryDue(GroupConfiguration.Configuration configs) {
        if(null != configs && null != configs.deadline) {
            return new Date(Long.valueOf(configs.deadline));
        }
        return null;
    }

    private SimpleAddress parseDeliveryAddress(GroupConfiguration.Configuration configs) {
        if (null == configs) {
            return new SimpleAddress(null, null, null, null);
        }
        return new SimpleAddress(
                configs.addressRecipient,
                configs.addressStreet,
                configs.addressPostalCode,
                configs.addressPostOffice);
    }
}
