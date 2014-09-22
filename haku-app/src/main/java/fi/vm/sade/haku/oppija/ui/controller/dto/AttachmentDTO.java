package fi.vm.sade.haku.oppija.ui.controller.dto;

public class AttachmentDTO {

    private String id;
    private String aoId;
    private String aoGroupId;
    private String receptionStatus;
    private String processingStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAoId() {
        return aoId;
    }

    public void setAoId(String aoId) {
        this.aoId = aoId;
    }

    public String getAoGroupId() {
        return aoGroupId;
    }

    public void setAoGroupId(String aoGroupId) {
        this.aoGroupId = aoGroupId;
    }

    public String getReceptionStatus() {
        return receptionStatus;
    }

    public void setReceptionStatus(String receptionStatus) {
        this.receptionStatus = receptionStatus;
    }

    public String getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
    }

    @Override
    public String toString() {
        return "{" +
          "id='" + id + '\'' +
          ", aoId='" + aoId + '\'' +
          ", aoGroupId='" + aoGroupId + '\'' +
          ", receptionStatus='" + receptionStatus + '\'' +
          ", processingStatus='" + processingStatus + '\'' +
          '}';
    }
}
