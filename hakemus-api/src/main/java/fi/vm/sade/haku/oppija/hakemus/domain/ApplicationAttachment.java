package fi.vm.sade.haku.oppija.hakemus.domain;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ApplicationAttachment implements Serializable {

    private final I18nText name;
    private final I18nText header;
    private final I18nText description;
    private final Date deadline;
    private final I18nText deliveryNote;
    private final Address address;

    @JsonCreator
    public ApplicationAttachment(@JsonProperty(value = "name") I18nText name,
                                 @JsonProperty(value = "header") I18nText header,
                                 @JsonProperty(value = "description")I18nText description,
                                 @JsonProperty(value = "deadline") Date deadline,
                                 @JsonProperty(value = "deliveryNote") I18nText deliveryNote,
                                 @JsonProperty(value = "address") Address address) {
        this.name = name;
        this.header = header;
        this.description = description;
        this.deadline = deadline;
        this.deliveryNote = deliveryNote;
        this.address = address;
    }

    public I18nText getName() {
        return name;
    }

    public I18nText getHeader() {
        return header;
    }

    public I18nText getDescription() {
        return description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public I18nText getDeliveryNote() {
        return deliveryNote;
    }

    public Address getAddress() {
        return address;
    }
}
