package fi.vm.sade.haku.oppija.lomake.domain;

import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.SimpleAddress;

import java.util.Date;
import java.util.Map;

public class ApplicationOptionAttachmentRequest {

    private final Expr condition;
    private final String applicationOptionId;
    private final Boolean groupOption;
    private final I18nText header;
    private final I18nText description;
    private final Date deliveryDue;
    private final Boolean useGroupAddress;
    private final SimpleAddress deliveryAddress;

    public ApplicationOptionAttachmentRequest(Expr condition, String applicationOptionId, Boolean groupOption,
                                              I18nText header, I18nText description, Date deliveryDue,
                                              Boolean useGroupAddress, SimpleAddress deliveryAddress) {
        this.condition = condition;
        this.applicationOptionId = applicationOptionId;
        this.groupOption = groupOption;
        this.header = header;
        this.description = description;
        this.deliveryDue = deliveryDue;
        this.useGroupAddress = useGroupAddress;
        this.deliveryAddress = deliveryAddress;
    }

    public boolean include(Map<String, String> context){
        return condition.evaluate(context);
    }

    public Expr getCondition() {
        return condition;
    }

    public String getApplicationOptionId() {
        return applicationOptionId;
    }

    public Boolean isGroupOption() {
        return groupOption;
    }

    public I18nText getHeader() {
        return header;
    }

    public I18nText getDescription() {
        return description;
    }

    public Date getDeliveryDue() {
        return deliveryDue;
    }

    public Boolean getUseGroupAddress() {
        return useGroupAddress;
    }

    public SimpleAddress getDeliveryAddress() {
        return deliveryAddress;
    }
}
