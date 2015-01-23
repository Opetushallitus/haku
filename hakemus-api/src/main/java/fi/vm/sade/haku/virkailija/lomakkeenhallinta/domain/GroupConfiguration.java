package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class GroupConfiguration {

    public enum GroupType {
        MAXIMUM_NUMBER_OF,  // maximum number of application options
        PRIORITY, // Priority levels // possible autoconfig
        CONSTRAINT_GROUP // allow_group_id / deny_group_id
        }

    private final String groupdId;
    private final GroupType type;
    private Map<String,String> configurations = new HashMap<String,String>();


    @JsonCreator
    public GroupConfiguration(@JsonProperty(value = "groupId") String groupdId,
      @JsonProperty(value = "type") final GroupType type,
      @JsonProperty(value = "configurations") final Map<String,String> configurations) {
        this.groupdId = groupdId;
        this.type = type;
        if (null != configurations && !configurations.isEmpty())
            this.configurations = new HashMap<String, String>(configurations);
    }

    public String getGroupdId() {
        return groupdId;
    }

    public GroupType getType() {
        return type;
    }

    public Map<String,String> getConfigurations() {
        return configurations;
    }
}
