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

    public Person(String firstNames, String nickName, String lastName, String socialSecurityNumber,
                  boolean noSocialSecurityNumber, String email, String sex, String homeCity,
                  boolean securityOrder, String language, String nationality, String contactLanguage) {
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
}
