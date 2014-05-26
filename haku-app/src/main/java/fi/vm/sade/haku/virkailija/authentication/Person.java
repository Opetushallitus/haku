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

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
        this.firstNames =  isNotBlank(firstNames) ? other.firstNames : this.firstNames;
        this.nickName = isNotBlank(nickName) ? other.nickName : this.nickName;
        this.lastName = isNotBlank(lastName) ? other.lastName : this.lastName;
        this.socialSecurityNumber = isNotBlank(socialSecurityNumber) ? other.socialSecurityNumber : this.socialSecurityNumber;
        this.socialSecurityNumber = isNotBlank(other.socialSecurityNumber) ? other.socialSecurityNumber : this.socialSecurityNumber;
        this.noSocialSecurityNumber = isBlank(this.socialSecurityNumber);
        this.email = isNotBlank(email) ? other.email : this.email;
        this.sex = isNotBlank(sex) ? other.sex : this.sex;
        this.homeCity = isNotBlank(homeCity) ? other.homeCity : this.homeCity;
        this.securityOrder = isNotBlank(securityOrder;
        this.language = isNotBlank(language) ? other.language : this.language;
        this.nationality = isNotBlank(nationality) ? other.nationality : this.nationality;
        this.contactLanguage = isNotBlank(contactLanguage) ? other.contactLanguage : this.contactLanguage;
        this.personOid = isNotBlank(personOid) ? other.personOid : this.personOid;
        this.studentOid = isNotBlank(studentOid) ? other.studentOid : this.studentOid;
        return this;
    }
}
