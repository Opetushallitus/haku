package fi.vm.sade.haku.oppija.hakemus.domain;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class Address implements Serializable {
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

    @Override
    public boolean equals(Object object) {
        if(!(object instanceof Address)) {
            return false;
        }
        Address other = (Address)object;
        return StringUtils.equals(this.recipient, other.recipient)
                && StringUtils.equals(this.streetAddress, other.streetAddress)
                && StringUtils.equals(this.streetAddress2, other.streetAddress2)
                && StringUtils.equals(this.postalCode, other.postalCode)
                && StringUtils.equals(this.postOffice, other.postOffice);
    }
}
