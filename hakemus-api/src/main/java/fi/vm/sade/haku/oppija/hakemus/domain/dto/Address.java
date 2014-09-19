package fi.vm.sade.haku.oppija.hakemus.domain.dto;

public class Address {
    private final String recipient;
    private final String streetAddress;
    private final String streetAddress2;
    private final String postalCode;
    private final String postOffice;

    public Address(String recipient, String streetAddress, String streetAddress2, String postalCode, String postOffice) {
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
