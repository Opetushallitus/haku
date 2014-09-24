package fi.vm.sade.haku.oppija.hakemus.domain;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class Address {
    private final String recipient;
    private final String streetAddress;
    private final String streetAddress2;
    private final String postalCode;
    private final String postOffice;

    @JsonCreator
    public Address(@JsonProperty(value = "recipient") String recipient,
                   @JsonProperty(value = "streetAddress") String streetAddress,
                   @JsonProperty(value = "streetAddress2") String streetAddress2,
                   @JsonProperty(value = "postalCode") String postalCode,
                   @JsonProperty(value = "postOffice") String postOffice) {
        this.recipient = recipient;
        this.streetAddress = streetAddress;
        this.streetAddress2 = streetAddress2;
        this.postalCode = postalCode;
        this.postOffice = postOffice;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPostOffice() {
        return postOffice;
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }
}
