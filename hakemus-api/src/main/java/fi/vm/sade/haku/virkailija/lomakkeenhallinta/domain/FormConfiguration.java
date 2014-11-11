package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter.ComplexObjectIdDeserializer;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter.SimpleObjectIdSerializer;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;


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

    // FormConfiguration oid
    @JsonProperty(value = "_id")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL, using = SimpleObjectIdSerializer.class)
    @JsonDeserialize(using = ComplexObjectIdDeserializer.class)
    private ObjectId id;

    //Application System oid
    private String applicationSystemId;

    private FormTemplateType formTemplateType;

    private List<GroupConfiguration> groupConfigurations;

    public FormConfiguration() {
        groupConfigurations = new ArrayList<GroupConfiguration>();
    }

    public FormConfiguration(final String applicationSystemId) {
        this.applicationSystemId = applicationSystemId;
        groupConfigurations = new ArrayList<GroupConfiguration>();
    }

    public FormConfiguration(final String applicationSystemId, final FormTemplateType formTemplateType) {
        this.applicationSystemId = applicationSystemId;
        this.formTemplateType = formTemplateType;
        groupConfigurations = new ArrayList<GroupConfiguration>();
    }

    @JsonCreator
    protected FormConfiguration(@JsonProperty(value = "applicationSystemId") String applicationSystemId,
                                @JsonProperty(value = "formTemplateType") FormTemplateType formTemplateType,
                                @JsonProperty(value = "groupConfigurations") List<GroupConfiguration> groupConfigurations) {
        this.applicationSystemId = applicationSystemId;
        this.formTemplateType = formTemplateType;
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

    public FormTemplateType getFormTemplateType() {
        return formTemplateType;
    }

    public List<GroupConfiguration> getGroupConfigurations() {
        return groupConfigurations;
    }
}
