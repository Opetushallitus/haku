package fi.vm.sade.oppija.lomake.domain;

import fi.vm.sade.oppija.lomake.domain.elements.Form;

import java.util.List;

public class ApplicationSystemBuilder {
    private String id;
    private Form form;
    private I18nText name;
    private List<ApplicationPeriod> applicationPeriods;
    private String applicationSystemType;
    private Integer hakukausiVuosi;
    private String hakukausiUri;

    public ApplicationSystemBuilder() {
        // NOP
    }

    public ApplicationSystemBuilder addId(String id) {
        this.id = id;
        return this;
    }
    public ApplicationSystemBuilder addForm(Form form) {
        this.form = form;
        return this;
    }
    public ApplicationSystemBuilder addName(I18nText name) {
        this.name = name;
        return this;
    }
    public ApplicationSystemBuilder addApplicationPeriods(List<ApplicationPeriod> applicationPeriods) {
        this.applicationPeriods = applicationPeriods;
        return this;
    }
    public ApplicationSystemBuilder addApplicationSystemType(String applicationSystemType) {
        this.applicationSystemType = applicationSystemType;
        return this;
    }
    public ApplicationSystemBuilder addHakukausiVuosi(Integer hakukausiVuosi) {
        this.hakukausiVuosi = hakukausiVuosi;
        return this;
    }
    public ApplicationSystemBuilder addHakukausiUri(String hakukausiUri) {
        this.hakukausiUri = hakukausiUri;
        return this;
    }
    public ApplicationSystem get() {
        return new ApplicationSystem(id, form, name, applicationPeriods,
                applicationSystemType, hakukausiVuosi, hakukausiUri);
    }
}
