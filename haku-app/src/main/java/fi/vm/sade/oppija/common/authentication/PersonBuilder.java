package fi.vm.sade.oppija.common.authentication;

public final class PersonBuilder {

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

    private PersonBuilder() {

    }

    public static PersonBuilder start() {
        return new PersonBuilder();
    }

    public Person get() {
        return new Person(firstNames, nickName, lastName, socialSecurityNumber, noSocialSecurityNumber, email, sex,
                homeCity, securityOrder, language, nationality, contactLanguage);
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

    public boolean isNoSocialSecurityNumber() {
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

    public boolean isSecurityOrder() {
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

}
