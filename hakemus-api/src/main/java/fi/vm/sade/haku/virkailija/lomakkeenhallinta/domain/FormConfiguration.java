package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;

import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter.ComplexObjectIdDeserializer;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter.SimpleObjectIdSerializer;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class FormConfiguration {

    public enum FormTemplateType {
        YHTEISHAKU_KEVAT,
        YHTEISHAKU_SYKSY,
        LISAHAKU_KEVAT,
        LISAHAKU_SYKSY,
        YHTEISHAKU_KEVAT_KORKEAKOULU,
        YHTEISHAKU_SYKSY_KORKEAKOULU,
        LISAHAKU_KEVAT_KORKEAKOULU,
        LISAHAKU_SYKSY_KORKEAKOULU,
        PERUSOPETUKSEN_JALKEINEN_VALMENTAVA,
        AMK_ERKAT_JA_OPOT,
        AMK_OPET
    }

    public enum FeatureFlag {
        // BUG-27: Kirjoita kaksoistutkinnon ammatillisen koulutuksen keskiarvo
        // ja toisen ammatillisen tutkinnon keskiarvo eri kenttiin tietokannassa
        erotteleAmmatillinenJaYoAmmatillinenKeskiarvo,
        lukioKeskiarvoVapaaehtoinen,
        lukioAmmatillinenKeskiarvoVapaaehtoinen,
        ammatillinenKeskiarvoVapaaehtoinen
    }

    // FormConfiguration oid
    @JsonProperty(value = "_id")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = SimpleObjectIdSerializer.class)
    @JsonDeserialize(using = ComplexObjectIdDeserializer.class)
    private ObjectId id;

    //Application System oid
    private String applicationSystemId;

    private FormTemplateType formTemplateType;

    private List<GroupConfiguration> groupConfigurations;

    private Map<FeatureFlag, Boolean> featureFlags;

    public FormConfiguration() {
        groupConfigurations = new ArrayList<GroupConfiguration>();
        featureFlags = new HashMap<FeatureFlag, Boolean>();
    }

    public FormConfiguration(final String applicationSystemId) {
        this.applicationSystemId = applicationSystemId;
        groupConfigurations = new ArrayList<GroupConfiguration>();
        featureFlags = new HashMap<FeatureFlag, Boolean>();
    }

    public FormConfiguration(final String applicationSystemId,
                             final FormTemplateType formTemplateType,
                             final Map<FeatureFlag, Boolean> featureFlags) {
        this(applicationSystemId);
        this.formTemplateType = formTemplateType;
        if (null == featureFlags)
            this.featureFlags = new HashMap<FeatureFlag, Boolean>();
        else
            this.featureFlags = new HashMap<FeatureFlag, Boolean>(featureFlags);
    }

    @JsonCreator
    protected FormConfiguration(@JsonProperty(value = "applicationSystemId") String applicationSystemId,
                                @JsonProperty(value = "formTemplateType") FormTemplateType formTemplateType,
                                @JsonProperty(value = "groupConfigurations") List<GroupConfiguration> groupConfigurations,
                                @JsonProperty(value = "featureFlags") Map<FeatureFlag, Boolean> featureFlags) {
        this(applicationSystemId, formTemplateType, featureFlags);
        if (null == groupConfigurations)
            this.groupConfigurations = new ArrayList<GroupConfiguration>();
        else
            this.groupConfigurations = new ArrayList<GroupConfiguration>(groupConfigurations);
    }

    public ObjectId getId() {
        return id;
    }

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    public void setFormTemplateType(FormTemplateType formTemplateType) {
        this.formTemplateType = formTemplateType;
    }

    public FormTemplateType getFormTemplateType() {
        return formTemplateType;
    }

    public List<GroupConfiguration> getGroupConfigurations() {
        return groupConfigurations;
    }

    public void addGroupConfiguration(GroupConfiguration toBeAdded) {
        if (null == toBeAdded)
            return;
        groupConfigurations.add(toBeAdded);
    }

    public void removeGroupConfiguration(GroupConfiguration toBeRemoved) {
        if (null == toBeRemoved)
            return;
        groupConfigurations.remove(toBeRemoved);
    }

    public Map<FeatureFlag, Boolean> getFeatureFlags() {
        return featureFlags;
    }

    public boolean getFeatureFlag(FeatureFlag feature) {
        return this.featureFlags.containsKey(feature) && this.featureFlags.get(feature);
    }

    public void setFeatureFlag(FeatureFlag feature, Boolean enabled) {
        this.featureFlags.put(feature, enabled);
    }
}
