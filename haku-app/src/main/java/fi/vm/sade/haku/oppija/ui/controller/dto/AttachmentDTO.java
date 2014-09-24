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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AttachmentDTO that = (AttachmentDTO) o;

        if (aoGroupId != null ? !aoGroupId.equals(that.aoGroupId) : that.aoGroupId != null) {
            return false;
        }
        if (aoId != null ? !aoId.equals(that.aoId) : that.aoId != null) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (processingStatus != null ? !processingStatus.equals(that.processingStatus) : that.processingStatus != null) {
            return false;
        }
        if (receptionStatus != null ? !receptionStatus.equals(that.receptionStatus) : that.receptionStatus != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (aoId != null ? aoId.hashCode() : 0);
        result = 31 * result + (aoGroupId != null ? aoGroupId.hashCode() : 0);
        result = 31 * result + (receptionStatus != null ? receptionStatus.hashCode() : 0);
        result = 31 * result + (processingStatus != null ? processingStatus.hashCode() : 0);
        return result;
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
