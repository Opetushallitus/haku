package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public final class SimpleAddress {
    private final String recipient;
    private final String street;
    private final String postCode;
    private final String postOffice;


    @JsonCreator
    public SimpleAddress(@JsonProperty(value = "recipient") String recipient, @JsonProperty(value = "street") final String street, @JsonProperty(value = "postOffice") final String postOffice, @JsonProperty(value = "postCode") final String postCode) {
        this.recipient = recipient;
        this.street = street;
        this.postOffice = postOffice;
        this.postCode = postCode;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getPostOffice() {
        return postOffice;
    }

    public String getStreet() {
        return street;
    }
}
