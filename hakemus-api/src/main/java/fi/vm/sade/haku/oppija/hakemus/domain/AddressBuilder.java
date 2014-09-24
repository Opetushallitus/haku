package fi.vm.sade.haku.oppija.hakemus.domain;

public class AddressBuilder {
    private String recipient;
    private String streetAddress;
    private String streetAddress2;
    private String postalCode;
    private String postOffice;

    public static AddressBuilder start() {
        return new AddressBuilder();
    }

    public AddressBuilder setRecipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public AddressBuilder setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
        return this;
    }

    public AddressBuilder setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public AddressBuilder setPostOffice(String postOffice) {
        this.postOffice = postOffice;
        return this;
    }

    public AddressBuilder setStreetAddress2(String streetAddress2) {
        this.streetAddress2 = streetAddress2;
        return this;
    }

    public Address build() {
        return new Address(recipient, streetAddress, streetAddress2, postalCode, postOffice);
    }
}
