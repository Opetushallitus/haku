package fi.vm.sade.oppija.haku.domain;

/**
 * @author jukka
 * @version 9/26/123:02 PM}
 * @since 1.1
 */
public class HakemusId {
    private final String applicationPeriodId;
    private final String formId;
    private final String categoryId;
    private final String userId;

    public HakemusId(String applicationPeriodId, String formId, String categoryId, String userid) {
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

    public boolean isUserKnown() {
        return userId != null;
    }

    @Override
    public String toString() {
        return applicationPeriodId + "-" + formId + "-" + userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HakemusId hakemusId = (HakemusId) o;

        if (applicationPeriodId != null ? !applicationPeriodId.equals(hakemusId.applicationPeriodId) : hakemusId.applicationPeriodId != null)
            return false;
        if (formId != null ? !formId.equals(hakemusId.formId) : hakemusId.formId != null) return false;
        if (userId != null ? !userId.equals(hakemusId.userId) : hakemusId.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = applicationPeriodId != null ? applicationPeriodId.hashCode() : 0;
        result = 31 * result + (formId != null ? formId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}
