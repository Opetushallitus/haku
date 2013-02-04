package fi.vm.sade.oppija.hakemus.domain.dto;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.User;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class ApplicationDTO {

    private String oid;
    private String asId;
    private Map<String, String> answers;
    private Map<String, String> additionalInfo;

    @JsonCreator
    public ApplicationDTO(@JsonProperty(value = "oid") final String oid,
                       @JsonProperty(value = "asId") final String asId,
                       @JsonProperty(value = "answers") Map<String, String> answers,
                       @JsonProperty(value = "additionalInfo") Map<String, String> additionalInfo) {
        this.oid = oid;
        this.asId = asId;
        this.answers = answers;
        this.additionalInfo = additionalInfo;
    }


    public ApplicationDTO(Application application) {
        this.oid = application.getOid();
        this.asId = application.getFormId().getApplicationPeriodId();
        this.answers = application.getVastauksetMerged();
        this.additionalInfo = null;
    }

    public String getAsId() {
        return asId;
    }

    public void setAsId(String asId) {
        this.asId = asId;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOid() {
        return oid;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }

    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }
}
