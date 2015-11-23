package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.organisaatio.api.model.types.OsoiteDTO;
import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;
import java.util.Set;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class AttachmentRequest {
    private final I18nText header;
    private final I18nText description;
    private final Date deliveryDue;

    private final Boolean overrideAddress;
    private final SimpleAddress deliveryAddress;
    private final String attachedToOptionId;

    @JsonCreator
    public AttachmentRequest(@JsonProperty(value = "header") final I18nText header, @JsonProperty(value = "description") final I18nText description,
      @JsonProperty(value = "deliveryDue") final  Date deliveryDue,  @JsonProperty(value = "overrideAddress") final Boolean overrideAddress,
      @JsonProperty(value = "deliveryAddress") final SimpleAddress deliveryAddress, @JsonProperty(value = "attachedToOption") final String attachedToOptionId) {
        this.header = header;
        this.description = description;
        this.deliveryDue = deliveryDue;
        this.overrideAddress = overrideAddress;
        this.deliveryAddress = deliveryAddress;
        this.attachedToOptionId = attachedToOptionId;
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

    public Boolean getOverrideAddress() {
        return overrideAddress;
    }


    public SimpleAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getAttachedToOptionId() {
        return attachedToOptionId;
    }
}
