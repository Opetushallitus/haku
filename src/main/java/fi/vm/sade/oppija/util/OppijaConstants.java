package fi.vm.sade.oppija.util;

/**
 * Holds information that is needed about the form or application structure.
 *
 * @author Hannu Lyytikainen
 */
public class OppijaConstants {

    /*
    names of the fields that hold the information about the education degree
    of a selected application option
    */
    public static final String[] AO_EDUCATION_DEGREE_KEYS = {"preference1-Koulutus-educationDegree", "preference2-Koulutus-educationDegree",
            "preference3-Koulutus-educationDegree", "preference4-Koulutus-educationDegree",
            "preference5-Koulutus-educationDegree"};

    /*
    element ids, can be used to retrieve specific information from an application
    */
    public static final String ELEMENT_ID_FIRST_NAMES = "Etunimet";
    public static final String ELEMENT_ID_NICKNAME = "Kutsumanimi";
    public static final String ELEMENT_ID_LAST_NAME = "Sukunimi";
    public static final String ELEMENT_ID_SOCIAL_SECURITY_NUMBER = "Henkilotunnus";
    public static final String ELEMENT_ID_EMAIL = "Sähköposti";
    public static final String ELEMENT_ID_SEX = "Sukupuoli";
    public static final String ELEMENT_ID_HOME_CITY = "kotikunta";
    public static final String ELEMENT_ID_LANGUAGE = "äidinkieli";
    public static final String ELEMENT_ID_NATIONALITY = "kansalaisuus";
    public static final String ELEMENT_ID_CONTACT_LANGUAGE = "äidinkieli";


}
