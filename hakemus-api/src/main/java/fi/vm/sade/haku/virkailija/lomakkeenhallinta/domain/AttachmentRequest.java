package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.organisaatio.api.model.types.OsoiteDTO;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;
import java.util.Set;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public final class AttachmentRequest {
    private final I18nText header;
    private final I18nText description;
    private final Date deliveryDue;

    private final Boolean useGroupAddress;
    private final Boolean useLopAddress;
    private final SimpleAddress deliveryAddress;
    private final String attachedToOptionId;

    @JsonCreator
    public AttachmentRequest(@JsonProperty(value = "header") final I18nText header, @JsonProperty(value = "description") final I18nText description,
      @JsonProperty(value = "deliveryDue") final  Date deliveryDue,  @JsonProperty(value = "useGroupAddress") final Boolean useGroupAddress,
      @JsonProperty(value = "deliveryAddress") final SimpleAddress deliveryAddress, @JsonProperty(value = "attachedToOption") final String attachedToOptionId,
      @JsonProperty(value = "useLopAddress") final Boolean useLopAddress) {
        this.header = header;
        this.description = description;
        this.deliveryDue = deliveryDue;
        this.useGroupAddress = useGroupAddress;
        this.deliveryAddress = deliveryAddress;
        this.attachedToOptionId = attachedToOptionId;
        this.useLopAddress = useLopAddress;
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


    public Boolean getUseLopAddress() {
        return useLopAddress;
    }

    public SimpleAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getAttachedToOptionId() {
        return attachedToOptionId;
    }
}
