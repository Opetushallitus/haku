package fi.vm.sade.haku.virkailija.lomakkeenhallinta.service;

import fi.vm.sade.haku.oppija.lomake.domain.AttachmentGroupAddress;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.GroupConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.SimpleAddress;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.GroupConfiguration.ConfigKey;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AttachmentGroupConfigurator {
    private final FormConfiguration formConfiguration;

    public AttachmentGroupConfigurator(FormConfiguration formConfiguration) {
        this.formConfiguration = formConfiguration;
    }

    public Map<String, AttachmentGroupAddress> configureAttachmentGroupAddresses() {
        Map<String, AttachmentGroupAddress> addresses = new HashMap<>();
        for (GroupConfiguration groupConfiguration : formConfiguration.getGroupConfigurations()){
            switch (groupConfiguration.getType()) {
                case hakukohde_liiteosoite:
                    addresses.put(groupConfiguration.getGroupId(), createLiiteOsoite(groupConfiguration.getConfigurations()));
                    break;
            }
        }
        return addresses;

    }

    private AttachmentGroupAddress createLiiteOsoite(Map<ConfigKey, String> configs) {
        return new AttachmentGroupAddress(
                parseUseFirstAoAddress(configs),
                parseDeliveryDue(configs),
                parseDeliveryAddress(configs)
        );
    }

    private boolean parseUseFirstAoAddress(Map<ConfigKey, String> configs) {
        if(configs.containsKey(ConfigKey.useFirstAoAddress)) {
            return Boolean.valueOf(configs.get(ConfigKey.useFirstAoAddress));
        }
        return true;
    }

    private Date parseDeliveryDue(Map<ConfigKey, String> configs) {
        if(configs.containsKey(ConfigKey.deadline)) {
            return new Date(Long.valueOf(configs.get(ConfigKey.deadline)));
        }
        return null;
    }

    private SimpleAddress parseDeliveryAddress(Map<ConfigKey, String> configs) {
        return new SimpleAddress(
                configs.get(ConfigKey.addressRecipient),
                configs.get(ConfigKey.addressStreet),
                configs.get(ConfigKey.addressPostalCode),
                configs.get(ConfigKey.addressPostOffice)
        );
    }
}
