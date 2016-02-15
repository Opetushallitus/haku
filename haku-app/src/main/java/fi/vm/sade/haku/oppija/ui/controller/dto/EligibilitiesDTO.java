package fi.vm.sade.haku.oppija.ui.controller.dto;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.Date;
import java.util.List;

public class EligibilitiesDTO {
    private List<AttachmentsAndEligibilityDTO> eligibilities;
    private Date updated;

    public List<AttachmentsAndEligibilityDTO> getEligibilities() {
        return eligibilities;
    }

    public void setEligibilities(List<AttachmentsAndEligibilityDTO> eligibilities) {
        this.eligibilities = eligibilities;
    }

    public Date getUpdated() {
        return updated;
    }

    @JsonDeserialize(using = DateDeserializer.class)
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EligibilitiesDTO that = (EligibilitiesDTO) o;

        if (eligibilities != null ? !eligibilities.equals(that.eligibilities) : that.eligibilities != null)
            return false;
        return updated != null ? updated.equals(that.updated) : that.updated == null;

    }

    @Override
    public int hashCode() {
        int result = eligibilities != null ? eligibilities.hashCode() : 0;
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EligibilitiesDTO{" +
                "eligibilities=" + eligibilities +
                ", updated=" + updated +
                '}';
    }
}
