package fi.vm.sade.haku.oppija.ui.controller.dto;

import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AttachmentsAndEligibilityDTO {

    // preference id
    private String aoId;
    private String status;
    private String maksuvelvollisuus;
    private String source;
    private String rejectionBasis;
    private Boolean preferencesChecked;
    private List<AttachmentDTO> attachments = new ArrayList<AttachmentDTO>();

    public String getMaksuvelvollisuus() {
        return maksuvelvollisuus;
    }

    public void setMaksuvelvollisuus(String maksuvelvollisuus) {
        this.maksuvelvollisuus = maksuvelvollisuus;
    }

    public String getAoId() {
        return aoId;
    }

    public void setAoId(String aoId) {
        this.aoId = aoId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRejectionBasis() {
        return rejectionBasis;
    }

    public void setRejectionBasis(String rejectionBasis) {
        this.rejectionBasis = rejectionBasis;
    }

    public Boolean getPreferencesChecked() {
        return preferencesChecked;
    }

    public void setPreferencesChecked(Boolean preferencesChecked) {
        this.preferencesChecked = preferencesChecked;
    }

    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = new ArrayList<AttachmentDTO>(attachments);
    }

    @Override
    public String toString() {
        return "AttachmentsAndEligibilityDTO{" +
          "aoId='" + aoId + '\'' +
          ", status='" + status + '\'' +
          ", source='" + source + '\'' +
          ", maksuvelvollisuus='" + maksuvelvollisuus + '\'' +
          ", rejectionBasis='" + rejectionBasis + '\'' +
          ", preferencesChecked=" + preferencesChecked +
          ", attachments= [" + StringUtils.join(attachments, ",") + "]" +
          '}';
    }
}
