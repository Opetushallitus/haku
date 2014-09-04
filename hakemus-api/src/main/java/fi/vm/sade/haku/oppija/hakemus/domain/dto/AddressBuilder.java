package fi.vm.sade.haku.oppija.hakemus.domain.dto;

public class AddressBuilder {
    private String recipient;
    private String streetAddress;
    private String streetAddress2;
    private String postalCode;
    private String postOffice;

    public static AddressBuilder start() {
        return new AddressBuilder();
    }

    public String getRecipient() {
        return recipient;
    }

    public AddressBuilder setRecipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public AddressBuilder setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public AddressBuilder setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getPostOffice() {
        return postOffice;
    }

    public AddressBuilder setPostOffice(String postOffice) {
        this.postOffice = postOffice;
        return this;
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public AddressBuilder setStreetAddress2(String streetAddress2) {
        this.streetAddress2 = streetAddress2;
        return this;
    }

    public Address build() {
        Address address = new Address();
        address.setPostalCode(postalCode);
        address.setPostOffice(postOffice);
        address.setRecipient(recipient);
        address.setStreetAddress(streetAddress);
        address.setStreetAddress2(streetAddress2);
        return address;
    }
}