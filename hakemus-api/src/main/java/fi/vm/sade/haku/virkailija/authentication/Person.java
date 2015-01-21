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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Hannu Lyytikainen
 */
public class Person {

    private String firstNames;
    private String nickName;
    private String lastName;
    private String socialSecurityNumber;
    private String dateOfBirth;
    private Boolean noSocialSecurityNumber;
    private String email;
    private String sex;
    private Boolean securityOrder;
    private String language;
    private String nationality;
    private String contactLanguage;
    private String personOid;
    private String studentOid;

    public Person(String firstNames, String nickName, String lastName, String socialSecurityNumber,
                  String dateOfBirth, Boolean noSocialSecurityNumber, String email, String sex, String homeCity,
                  Boolean securityOrder, String language, String nationality, String contactLanguage,
                  String personOid, String studentOid) {
        this.firstNames = firstNames;
        this.nickName = nickName;
        this.lastName = lastName;
        this.socialSecurityNumber = socialSecurityNumber;
        this.dateOfBirth = dateOfBirth;
        this.noSocialSecurityNumber = noSocialSecurityNumber;
        this.email = email;
        this.sex = sex;
        this.securityOrder = securityOrder;
        this.language = language;
        this.nationality = nationality;
        this.contactLanguage = contactLanguage;
        this.personOid = personOid;
        this.studentOid = studentOid;
    }

    public Person(String firstNames, String lastName, String socialSecurityNumber, String email, String studentOid, String dateOfBirth) {
        this.firstNames = firstNames;
        this.lastName = lastName;
        this.socialSecurityNumber = socialSecurityNumber;
        this.email = email;
        this.personOid = studentOid;
        this.studentOid = studentOid;
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

    public Boolean isNoSocialSecurityNumber() {
        return noSocialSecurityNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getSex() {
        return sex;
    }

    public Boolean isSecurityOrder() {
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
                .append(" dateOfBirth: ").append(dateOfBirth)
                .append(" noSocialSecurityNumber: ").append(noSocialSecurityNumber)
                .append(" email: ").append(email)
                .append(" sex: ").append(sex)
                .append(" securityOrder: ").append(securityOrder)
                .append(" language: ").append(language)
                .append(" nationality: ").append(nationality)
                .append(" contactLanguage: ").append(contactLanguage)
                .append(" personOid: ").append(personOid)
                .append(" studentOid: ").append(studentOid);
        return sb.toString();
    }

    public Person mergeWith(Person other) {
        this.firstNames =  isNotBlank(other.firstNames) ? other.firstNames : this.firstNames;
        this.nickName = isNotBlank(other.nickName) ? other.nickName : this.nickName;
        this.lastName = isNotBlank(other.lastName) ? other.lastName : this.lastName;
        this.socialSecurityNumber = isNotBlank(other.socialSecurityNumber) ? other.socialSecurityNumber : this.socialSecurityNumber;
        this.dateOfBirth = isNotBlank(other.dateOfBirth) ? other.dateOfBirth : this.dateOfBirth;
        this.noSocialSecurityNumber = other.noSocialSecurityNumber != null ? other.noSocialSecurityNumber : this.noSocialSecurityNumber;
        this.email = isNotBlank(other.email) ? other.email : this.email;
        this.sex = isNotBlank(other.sex) ? other.sex : this.sex;
        this.securityOrder = other.securityOrder != null ? other.securityOrder : this.securityOrder;
        this.language = isNotBlank(other.language) ? other.language : this.language;
        this.nationality = isNotBlank(other.nationality) ? other.nationality : this.nationality;
        this.contactLanguage = isNotBlank(other.contactLanguage) ? other.contactLanguage : this.contactLanguage;
        this.personOid = isNotBlank(other.personOid) ? other.personOid : this.personOid;
        this.studentOid = isNotBlank(other.studentOid) ? other.studentOid : this.studentOid;
        return this;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
