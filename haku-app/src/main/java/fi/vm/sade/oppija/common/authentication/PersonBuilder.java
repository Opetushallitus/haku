package fi.vm.sade.oppija.common.authentication;
/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

public class PersonBuilder {


    private String firstNames;
    private String nickName;
    private String lastName;
    private String socialSecurityNumber;
    private boolean noSocialSecurityNumber;
    private String email;
    private String sex;
    private String homeCity;
    private boolean securityOrder;
    private String language;
    private String nationality;
    private String contactLanguage;

    public Person get() {
        return new Person(firstNames, nickName, lastName, socialSecurityNumber, noSocialSecurityNumber, email,
                sex, homeCity, securityOrder, language, nationality, contactLanguage);
    }

    public PersonBuilder setFirstNames(String firstNames) {
        this.firstNames = firstNames;
        return this;
    }

    public PersonBuilder setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public PersonBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public PersonBuilder setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
        return this;
    }

    public PersonBuilder setNoSocialSecurityNumber(boolean noSocialSecurityNumber) {
        this.noSocialSecurityNumber = noSocialSecurityNumber;
        return this;
    }

    public PersonBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public PersonBuilder setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public PersonBuilder setHomeCity(String homeCity) {
        this.homeCity = homeCity;
        return this;
    }

    public PersonBuilder setSecurityOrder(boolean securityOrder) {
        this.securityOrder = securityOrder;
        return this;
    }

    public PersonBuilder setLanguage(String language) {
        this.language = language;
        return this;
    }

    public PersonBuilder setNationality(String nationality) {
        this.nationality = nationality;
        return this;
    }

    public PersonBuilder setContactLanguage(String contactLanguage) {
        this.contactLanguage = contactLanguage;
        return this;
    }
}
