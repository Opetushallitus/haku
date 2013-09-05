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
package fi.vm.sade.oppija.common.authentication;

/**
 * @author Hannu Lyytikainen
 */
public class Person {

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
    private String dateOfBirth;

    public Person(String firstNames, String nickName, String lastName, String socialSecurityNumber,
                  boolean noSocialSecurityNumber, String email, String sex, String homeCity,
                  boolean securityOrder, String language, String nationality, String contactLanguage, String dateOfBirth) {
        this.firstNames = firstNames;
        this.nickName = nickName;
        this.lastName = lastName;
        this.socialSecurityNumber = socialSecurityNumber;
        this.noSocialSecurityNumber = noSocialSecurityNumber;
        this.email = email;
        this.sex = sex;
        this.homeCity = homeCity;
        this.securityOrder = securityOrder;
        this.language = language;
        this.nationality = nationality;
        this.contactLanguage = contactLanguage;
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstNames() {
        return firstNames;
    }

    public String getNickName() {
        return nickName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public boolean isNoSocialSecurityNumber() {
        return noSocialSecurityNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getSex() {
        return sex;
    }

    public String getHomeCity() {
        return homeCity;
    }

    public boolean isSecurityOrder() {
        return securityOrder;
    }

    public String getLanguage() {
        return language;
    }

    public String getNationality() {
        return nationality;
    }

    public String getContactLanguage() {
        return contactLanguage;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

}
