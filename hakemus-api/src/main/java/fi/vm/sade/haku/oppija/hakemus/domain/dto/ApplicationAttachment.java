package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import fi.vm.sade.haku.oppija.hakemus.domain.Address;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;

import java.util.Date;

public class ApplicationAttachment {

    private I18nText name;
    private I18nText header;
    private I18nText description;
    private Date deadline;
    private Address address;

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
