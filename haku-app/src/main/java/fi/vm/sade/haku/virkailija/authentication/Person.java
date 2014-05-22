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
package fi.vm.sade.haku.virkailija.authentication;

import org.apache.commons.lang3.StringUtils;

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
    private String personOid;
    private String studentOid;

    public Person(String firstNames, String nickName, String lastName, String socialSecurityNumber,
                  boolean noSocialSecurityNumber, String email, String sex, String homeCity,
                  boolean securityOrder, String language, String nationality, String contactLanguage,
                  String personOid, String studentOid) {
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
        this.personOid = personOid;
        this.studentOid = studentOid;
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

    public String getPersonOid() {
        return personOid;
    }

    public String getStudentOid() {
        return studentOid;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("firstNames: ").append(firstNames)
                .append(" nickName: ").append(nickName)
                .append(" lastName: ").append(lastName)
                .append(" socialSecurityNumber: ").append(socialSecurityNumber)
                .append(" noSocialSecurityNumber: ").append(noSocialSecurityNumber)
                .append(" email: ").append(email)
                .append(" sex: ").append(sex)
                .append(" homeCity: ").append(homeCity)
                .append(" securityOrder: ").append(securityOrder)
                .append(" language: ").append(language)
                .append(" nationality: ").append(nationality)
                .append(" contactLanguage: ").append(contactLanguage)
                .append(" personOid: ").append(personOid)
                .append(" studentOid: ").append(studentOid);
        return sb.toString();

    }

    public Person mergeWith(Person other) {
        this.firstNames = other.firstNames != null ? other.firstNames : this.firstNames;
        this.nickName = other.nickName != null ? other.nickName : this.nickName;
        this.lastName = other.lastName != null ? other.lastName : this.lastName;
        this.socialSecurityNumber = other.socialSecurityNumber != null ? other.socialSecurityNumber : this.socialSecurityNumber;
        this.socialSecurityNumber = other.socialSecurityNumber != null ? other.socialSecurityNumber : this.socialSecurityNumber;
        this.noSocialSecurityNumber = StringUtils.isBlank(this.socialSecurityNumber);
        this.email = other.email != null ? other.email : this.email;
        this.sex = other.sex != null ? other.sex : this.sex;
        this.homeCity = other.homeCity != null ? other.homeCity : this.homeCity;
        this.securityOrder = other.securityOrder;
        this.language = other.language != null ? other.language : this.language;
        this.nationality = other.nationality != null ? other.nationality : this.nationality;
        this.contactLanguage = other.contactLanguage != null ? other.contactLanguage : this.contactLanguage;
        this.personOid = other.personOid != null ? other.personOid : this.personOid;
        this.studentOid = other.studentOid != null ? other.studentOid : this.studentOid;
        return this;
    }
}
