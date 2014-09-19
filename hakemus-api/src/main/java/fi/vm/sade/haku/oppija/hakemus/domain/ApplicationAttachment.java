package fi.vm.sade.haku.oppija.hakemus.domain;

import fi.vm.sade.haku.oppija.hakemus.domain.Address;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ApplicationAttachment {

    private final I18nText name;
    private final I18nText header;
    private final I18nText description;
    private final Date deadline;
    private final Address address;

    @JsonCreator
    public ApplicationAttachment(@JsonProperty(value = "name") I18nText name,
                                 @JsonProperty(value = "header") I18nText header,
                                 @JsonProperty(value = "description")I18nText description,
                                 @JsonProperty(value = "deadline") Date deadline,
                                 @JsonProperty(value = "address") Address address) {
        this.name = name;
        this.header = header;
        this.description = description;
        this.deadline = deadline;
        this.address = address;
    }

    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public I18nText getHeader() {
        return header;
    }

    public void setHeader(I18nText header) {
        this.header = header;
    }

    public I18nText getDescription() {
        return description;
    }

    public void setDescription(I18nText description) {
        this.description = description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


}
