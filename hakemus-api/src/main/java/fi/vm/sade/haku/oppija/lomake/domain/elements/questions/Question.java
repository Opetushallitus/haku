package fi.vm.sade.haku.oppija.lomake.domain.elements.questions;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;
import org.springframework.data.annotation.Transient;

public abstract class Question extends Titled {
    private String applicationOptionId;
    private String applicationOptionGroupId;

    public Question(String id, I18nText i18nText) {
        super(id, i18nText);
    }

    public String getApplicationOptionId() {
        return applicationOptionId;
    }

    public void setApplicationOptionId(String applicationOptionId) {
        this.applicationOptionId = applicationOptionId;
    }

    public String getApplicationOptionGroupId() {
        return applicationOptionGroupId;
    }

    public void setApplicationOptionGroupId(String applicationOptionGroupId) {
        this.applicationOptionGroupId = applicationOptionGroupId;
    }

    @Transient
    public String getExcelValue(String answer, String lang) {
        return answer;
    }
}
