package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class GroupConfiguration {

    //TODO: =RS= Siirrä käyttämään koodistoa ml organisaatiopalvelu.
    public enum GroupType {
        hakukohde_liiteosoite, // common address
        hakukohde_rajaava,  // maximum number of application options
        hakukohde_priorisoiva, // Priority levels // possible autoconfig
        CONSTRAINT_GROUP // allow_group_id / deny_group_id  -- NOT IMPLEMENTED
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    public static class Configuration {
        public final String maximumNumberOf;
        public final String useFirstAoAddress;
        public final String addressRecipient;
        public final String addressStreet;
        public final String addressPostalCode;
        public final String addressPostOffice;
        public final String deadline;
        public final I18nText helpText;

        @JsonCreator
        public Configuration(@JsonProperty(value = "maximumNumberOf") String maximumNumberOf,
                             @JsonProperty(value = "useFirstAoAddress") String useFirstAoAddress,
                             @JsonProperty(value = "addressRecipient") String addressRecipient,
                             @JsonProperty(value = "addressStreet") String addressStreet,
                             @JsonProperty(value = "addressPostalCode") String addressPostalCode,
                             @JsonProperty(value = "addressPostOffice") String addressPostOffice,
                             @JsonProperty(value = "deadline") String deadline,
                             @JsonProperty(value = "helpText") I18nText helpText) {
            this.maximumNumberOf = maximumNumberOf;
            this.useFirstAoAddress = useFirstAoAddress;
            this.addressRecipient = addressRecipient;
            this.addressStreet = addressStreet;
            this.addressPostalCode = addressPostalCode;
            this.addressPostOffice = addressPostOffice;
            this.deadline = deadline;
            this.helpText = helpText;
        }
    }

    private final String groupId;
    private final GroupType type;
    private final Configuration configurations;

    @JsonCreator
    public GroupConfiguration(@JsonProperty(value = "groupId") String groupId,
      @JsonProperty(value = "type") final GroupType type,
      @JsonProperty(value = "configurations") final Configuration configurations) {
        this.groupId = groupId;
        this.type = type;
        this.configurations = configurations;
    }

    public String getGroupId() {
        return groupId;
    }

    public GroupType getType() {
        return type;
    }

    public Configuration getConfigurations() {
        return configurations;
    }
}
