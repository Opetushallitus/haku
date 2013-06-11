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
package fi.vm.sade.oppija.lomakkeenhallinta.util;

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

    public static final String PHASE_ID_CONTACT = "henkilotiedot";

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

    public static final String VERBOSE_HELP = " Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
            "Curabitur nec dolor quam. Duis sodales placerat scelerisque. Suspendisse " +
            "porta mauris eu felis malesuada rutrum. Aliquam varius fringilla mi sed " +
            "luctus. Nam in enim ipsum. Sed lobortis lorem sit amet justo " +
            "blandit et tempus ante eleifend. Proin egestas, magna et condimentum egestas, " +
            "arcu mauris tincidunt augue, eget varius diam massa nec " +
            "nisi. Proin dolor risus, tincidunt non faucibus imperdiet, fringilla quis massa. " +
            "Curabitur pharetra posuere est, sit amet pulvinar urna " +
            "facilisis at. Praesent posuere feugiat elit vel porttitor. Integer venenatis, " +
            "arcu ac suscipit ornare, augue nibh tempus libero, eget " +
            "molestie turpis massa quis purus. Suspendisse id libero dolor. Ut eget velit augue, " +
            "eget fringilla erat. Quisque sed neque non arcu " +
            "elementum vehicula eget at est. Etiam dictum fringilla mi, sit amet sodales tortor facilisis in.\n"
            + "\n"
            + "Nunc nisl felis, placerat non pellentesque non, dapibus non sem. " +
            "Nunc et consectetur tellus. Class aptent taciti sociosqu ad litora " +
            "torquent per conubia nostra, per inceptos himenaeos. Nulla facilisi. " +
            "Nulla facilisi. Etiam lobortis, justo non eleifend rhoncus, eros " +
            "felis vestibulum metus, ut ullamcorper neque urna et velit. " +
            "Duis congue tincidunt urna non consectetur. Phasellus quis ligula et libero " +
            "convallis eleifend non quis velit. Morbi luctus, ligula sed mollis placerat, " +
            "nunc justo tempor velit, eget dignissim ante ipsum eu elit. " +
            "Sed interdum urna in justo eleifend id fringilla mi facilisis. " +
            "Ut id sapien erat. Aenean urna quam, aliquet nec imperdiet quis, suscipit " +
            "eu nunc. Vestibulum vitae dolor in sapien auctor hendrerit et et turpis. " +
            "Ut at diam eu sapien blandit blandit at in lorem.\n"
            + "\n"
            + "Aenean ornare, mi non rutrum gravida, augue neque pretium leo, " +
            "in porta justo mauris eget orci. Donec porttitor eleifend aliquam. " +
            "Cras mattis tincidunt purus, et facilisis risus consequat vitae. " +
            "Nunc consectetur, odio sit amet rhoncus iaculis, ipsum lectus pharetra " +
            "lectus, sit amet vestibulum est mi commodo enim. Sed libero sem, " +
            "iaculis a lobortis non, molestie id arcu. Donec gravida tincidunt ligula " +
            "quis mattis. Nulla sit amet malesuada sem. " +
            "Duis porta adipiscing purus iaculis consequat. Aliquam erat volutpat. ";

}
