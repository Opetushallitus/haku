package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;


/**
 * @author jukka
 * @version 9/26/123:02 PM}
 * @since 1.1
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class HakemusId implements Serializable {

    private static final long serialVersionUID = 8484849312020479901L;
    
    private final String applicationPeriodId;
    private final String formId;
    private final String categoryId;
    private final String userId;

    public HakemusId(@JsonProperty(value = "applicationPeriodId") String applicationPeriodId, @JsonProperty(value = "formId") String formId, @JsonProperty(value = "categoryId") String categoryId, @JsonProperty(value = "userId") String userid) {
        this.applicationPeriodId = applicationPeriodId;
        this.formId = formId;
        this.categoryId = categoryId;
        this.userId = userid;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getUserId() {
        return userId;
    }

    public String getFormId() {
        return formId;
    }

    public String getApplicationPeriodId() {
        return applicationPeriodId;
    }

    @JsonIgnore
    public boolean isUserKnown() {
        return userId != null;
    }

    public String asKey() {
        return applicationPeriodId + '_' + formId + "_" + categoryId + "_" + userId;
    }

    public static HakemusId fromKey(String key) {
        final String[] split = key.split("_");
        return new HakemusId(split[0], split[1], split[2], split[3]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HakemusId hakemusId = (HakemusId) o;

        if (applicationPeriodId != null ? !applicationPeriodId.equals(hakemusId.applicationPeriodId) : hakemusId.applicationPeriodId != null)
            return false;
        if (categoryId != null ? !categoryId.equals(hakemusId.categoryId) : hakemusId.categoryId != null) return false;
        if (formId != null ? !formId.equals(hakemusId.formId) : hakemusId.formId != null) return false;
        if (userId != null ? !userId.equals(hakemusId.userId) : hakemusId.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = applicationPeriodId != null ? applicationPeriodId.hashCode() : 0;
        result = 31 * result + (formId != null ? formId.hashCode() : 0);
        result = 31 * result + (categoryId != null ? categoryId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}
