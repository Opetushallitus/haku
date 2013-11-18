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
package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

/**
 * Holds information that is needed about the form or application structure.
 *
 * @author Hannu Lyytikainen
 */
public final class OppijaConstants {

    private OppijaConstants() {
        // Utility class, no need for instances
    }

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
    public static final String ELEMENT_ID_FIRST_LANGUAGE = "äidinkieli";
    public static final String ELEMENT_ID_CONTACT_LANGUAGE = "asiointikieli";

    public static final String ELEMENT_ID_BASE_EDUCATION = "POHJAKOULUTUS";

    public static final String PREFERENCE_ID = "preference%d-Koulutus-id";
    public static final String PREFERENCE_NAME = "preference%d-Koulutus";
    public static final String PREFERENCE_ORGANIZATION = "preference%d-Opetuspiste";
    public static final String PREFERENCE_DISCRETIONARY = "preference%d-discretionary";

    public static final String NATIONALITY_CODE_FI = "FIN";

    public static final String PERUSKOULU = "1";
    public static final String YLIOPPILAS = "9";
    public static final String OSITTAIN_YKSILOLLISTETTY = "2";
    public static final String ERITYISOPETUKSEN_YKSILOLLISTETTY = "3";
    public static final String YKSILOLLISTETTY = "6";
    public static final String KESKEYTYNYT = "7";
    public static final String ULKOMAINEN_TUTKINTO = "0";

    public static final String VARSINAINEN_HAKU = "hakutyyppi_01";
    public static final String LISA_HAKU = "hakutyyppi_03";

    public static final String HAKUKAUSI_SYKSY = "kausi_s";
    public static final String HAKUKAUSI_KEVAT = "kausi_k";
}
