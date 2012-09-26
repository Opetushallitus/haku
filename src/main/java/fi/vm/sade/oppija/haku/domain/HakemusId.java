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
}
