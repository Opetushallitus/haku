package fi.vm.sade.haku.virkailija.authentication;

public final class PersonBuilder {

    private String firstNames;
    private String nickName;
    private String lastName;
    private String socialSecurityNumber;
    private String dateOfBirth;
    private Boolean noSocialSecurityNumber;
    private String email;
    private String sex;
    private String homeCity;
    private Boolean securityOrder;
    private String language;
    private String nationality;
    private String contactLanguage;
    private String personOid;
    private String studentOid;
    private String phone;
    private String address;
    private String postalCode;
    private String postalCity;
    private String countryOfResidence;

    private PersonBuilder() {

    }

    public static PersonBuilder start() {
        return new PersonBuilder();
    }

    public static PersonBuilder start(Person person) {
        PersonBuilder builder = new PersonBuilder();
        builder
                .setFirstNames(person.getFirstNames())
                .setNickName(person.getNickName())
                .setLastName(person.getLastName())
                .setSocialSecurityNumber(person.getSocialSecurityNumber())
                .setDateOfBirth(person.getDateOfBirth())
                .setNoSocialSecurityNumber(person.isNoSocialSecurityNumber())
                .setEmail(person.getEmail())
                .setSex(person.getSex())
                .setSecurityOrder(person.isSecurityOrder())
                .setLanguage(person.getLanguage())
                .setNationality(person.getNationality())
                .setContactLanguage(person.getContactLanguage())
                .setPersonOid(person.getPersonOid())
                .setStudentOid(person.getStudentOid())
                .setPhone(person.getPhone())
                .setAddress(person.getAddress())
                .setPostalCode(person.getPostalCode())
                .setPostalCity(person.getPostalCity())
                .setCountryOfResidence(person.getCountryOfResidence());

        return builder;
    }

    public Person get() {
        return new Person(firstNames, nickName, lastName, socialSecurityNumber, dateOfBirth, noSocialSecurityNumber,
                email, sex, homeCity, securityOrder, language, nationality, contactLanguage, personOid, studentOid,
                phone, address, postalCode, postalCity, countryOfResidence);
    }

    public String getFirstNames() {
        return firstNames;
    }

    public PersonBuilder setFirstNames(String firstNames) {
        this.firstNames = firstNames;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public PersonBuilder setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public PersonBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public PersonBuilder setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
        return this;
    }

    public Boolean isNoSocialSecurityNumber() {
        return noSocialSecurityNumber;
    }

    public PersonBuilder setNoSocialSecurityNumber(boolean noSocialSecurityNumber) {
        this.noSocialSecurityNumber = noSocialSecurityNumber;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public PersonBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public PersonBuilder setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public String getHomeCity() {
        return homeCity;
    }

    public PersonBuilder setHomeCity(String homeCity) {
        this.homeCity = homeCity;
        return this;
    }

    public Boolean isSecurityOrder() {
        return securityOrder;
    }

    public PersonBuilder setSecurityOrder(boolean securityOrder) {
        this.securityOrder = securityOrder;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public PersonBuilder setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getNationality() {
        return nationality;
    }

    public PersonBuilder setNationality(String nationality) {
        this.nationality = nationality;
        return this;
    }

    public String getContactLanguage() {
        return contactLanguage;
    }

    public PersonBuilder setContactLanguage(String contactLanguage) {
        this.contactLanguage = contactLanguage;
        return this;
    }

    public String getPersonOid() {
        return personOid;
    }

    public PersonBuilder setPersonOid(String personOid) {
        this.personOid = personOid;
        return this;
    }

    public String getStudentOid() {
        return studentOid;
    }

    public PersonBuilder setStudentOid(String studentOid) {
        this.studentOid = studentOid;
        return this;
    }

    public PersonBuilder setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public PersonBuilder setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public PersonBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public PersonBuilder setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getPostalCity() {
        return postalCity;
    }

    public PersonBuilder setPostalCity(String postalCity) {
        this.postalCity = postalCity;
        return this;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public PersonBuilder setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
        return this;
    }
}
