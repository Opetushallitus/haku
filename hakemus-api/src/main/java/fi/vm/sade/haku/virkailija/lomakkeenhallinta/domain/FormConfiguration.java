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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class FormConfiguration {

    public enum FormTemplateType {
        YHTEISHAKU_KEVAT,
        YHTEISHAKU_SYKSY,
        LISAHAKU_SYKSY,
        LISAHAKU_KEVAT,
        YHTEISHAKU_SYKSY_KORKEAKOULU,
        YHTEISHAKU_KEVAT_KORKEAKOULU,
        LISAHAKU_SYKSY_KORKEAKOULU,
        LISAHAKU_KEVAT_KORKEAKOULU,
        PERUSOPETUKSEN_JALKEINEN_VALMENTAVA
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

    public FormConfiguration(){
        groupConfigurations = new ArrayList<GroupConfiguration>();
    }

    public FormConfiguration(final String applicationSystemId){
        this.applicationSystemId = applicationSystemId;
        groupConfigurations = new ArrayList<GroupConfiguration>();
    }

    public FormConfiguration(final String applicationSystemId, final FormTemplateType formTemplateType){
        this.applicationSystemId = applicationSystemId;
        this.formTemplateType = formTemplateType;
        groupConfigurations = new ArrayList<GroupConfiguration>();
    }

    @JsonCreator
    protected FormConfiguration(@JsonProperty(value = "applicationSystemId") String applicationSystemId,
      @JsonProperty(value = "formTemplateType") FormTemplateType formTemplateType,
      @JsonProperty(value = "groupConfigurations") List<GroupConfiguration> groupConfigurations){
        this.applicationSystemId = applicationSystemId;
        this.formTemplateType = formTemplateType;
        this.groupConfigurations = new ArrayList(groupConfigurations);
    }

    public static FormConfiguration.FormTemplateType figureOutFormForApplicationSystem(final ApplicationSystem as) {
        if (OppijaConstants.KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA.equals(as.getKohdejoukkoUri())) {
            return FormConfiguration.FormTemplateType.PERUSOPETUKSEN_JALKEINEN_VALMENTAVA;
        } else if (OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(as.getKohdejoukkoUri())) {
            return FormConfiguration.FormTemplateType.YHTEISHAKU_SYKSY_KORKEAKOULU;
        }
        if (as.getApplicationSystemType().equals(OppijaConstants.HAKUTYYPPI_LISAHAKU)) {
            if (as.getHakukausiUri().equals(OppijaConstants.HAKUKAUSI_KEVAT)) {
                return FormConfiguration.FormTemplateType.LISAHAKU_KEVAT;
            }
            return FormConfiguration.FormTemplateType.LISAHAKU_SYKSY;
        } else {
            if (as.getHakukausiUri().equals(OppijaConstants.HAKUKAUSI_SYKSY)) {
                return FormConfiguration.FormTemplateType.YHTEISHAKU_SYKSY;
            } else if (as.getHakukausiUri().equals(OppijaConstants.HAKUKAUSI_KEVAT)) {
                return FormConfiguration.FormTemplateType.YHTEISHAKU_KEVAT;
            } else {
                return FormConfiguration.FormTemplateType.PERUSOPETUKSEN_JALKEINEN_VALMENTAVA;
            }
        }
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
