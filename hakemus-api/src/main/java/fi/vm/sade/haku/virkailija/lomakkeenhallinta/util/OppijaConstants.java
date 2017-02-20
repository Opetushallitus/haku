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

import com.google.common.collect.Sets;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;

import java.util.*;

import static java.util.Arrays.asList;

/**
 * Holds information that is needed about the form or application structure.
 *
 * @author Hannu Lyytikainen
 */
public final class OppijaConstants {

    private OppijaConstants() {
        // Utility class, no need for instances
    }

    public static String FORM_COMMON_BUNDLE_NAME = "form_common";
    public static String MESSAGES_BUNDLE_NAME = "messages";

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
    public static final String ELEMENT_ID_PERSON_OID = "personOid";
    public static final String ELEMENT_ID_STUDENT_OID = "studentOid";
    public static final String ELEMENT_ID_FIRST_NAMES = "Etunimet";
    public static final String ELEMENT_ID_NICKNAME = "Kutsumanimi";
    public static final String ELEMENT_ID_LAST_NAME = "Sukunimi";
    public static final String ELEMENT_ID_SOCIAL_SECURITY_NUMBER = "Henkilotunnus";
    public static final String ELEMENT_ID_HAS_SOCIAL_SECURITY_NUMBER = "onkoSinullaSuomalainenHetu";
    public static final String ELEMENT_ID_SECURITY_ORDER = "turvakielto";
    public static final String ELEMENT_ID_DATE_OF_BIRTH = "syntymaaika";
    public static final Set<String> HENKILOTUNNUS_BASED_ELEMENT_IDS = Collections.unmodifiableSet(Sets.newHashSet(SocialSecurityNumber.HENKILOTUNNUS_HASH, ELEMENT_ID_DATE_OF_BIRTH, ELEMENT_ID_HAS_SOCIAL_SECURITY_NUMBER));

    public static final String ELEMENT_ID_EMAIL = "Sähköposti";
    public static final String ELEMENT_ID_EMAIL_DOUBLE = "SähköpostiUudelleen";
    public static final String ELEMENT_ID_HUOLTAJANSAHKOPOSTI = "huoltajansahkoposti";
    public static final String ELEMENT_ID_COUNTRY_OF_RESIDENCY = "asuinmaa";
    public static final String ELEMENT_VALUE_COUNTRY_OF_RESIDENCY_FIN = "FIN";
    public static final String ELEMENT_ID_FIN_ADDRESS = "lahiosoite";
    public static final String ELEMENT_ID_ADDRESS_ABROAD = "osoiteUlkomaa";
    public static final String ELEMENT_ID_FIN_POSTAL_NUMBER = "Postinumero";
    public static final String ELEMENT_ID_POSTAL_NUMBER_ABROAD = "postinumeroUlkomaa";
    public static final String ELEMENT_ID_PREFIX_PHONENUMBER = "matkapuhelinnumero";
    public static final String ELEMENT_ID_SEX = "sukupuoli";
    public static final String ELEMENT_ID_SENDING_SCHOOL = "lahtokoulu";
    public static final String ELEMENT_ID_SENDING_CLASS = "lahtoluokka";
    public static final String ELEMENT_ID_CLASS_LEVEL = "luokkataso";
    public static final Set<String> SENDING_SCHOOL_ELEMENT_IDS = Collections.unmodifiableSet(Sets.newHashSet(ELEMENT_ID_SENDING_SCHOOL, ELEMENT_ID_SENDING_CLASS, ELEMENT_ID_CLASS_LEVEL));
    public static final String ELEMENT_ID_HOME_CITY = "kotikunta";
    public static final String ELEMENT_ID_CITY_ABROAD = "kaupunkiUlkomaa";

    public static final String ELEMENT_ID_LANGUAGE = "aidinkieli";
    public static final String ELEMENT_ID_NATIONALITY = "kansalaisuus";
    public static final String ELEMENT_ID_FIRST_LANGUAGE = "äidinkieli";
    public static final String ELEMENT_ID_CONTACT_LANGUAGE = "asiointikieli";
    public static final String ELEMENT_ID_BASE_EDUCATION = "POHJAKOULUTUS";
    public static final String ELEMENT_ID_LISAKOULUTUS_KYMPPI = "LISAKOULUTUS_KYMPPI";
    public static final String ELEMENT_ID_LISAKOULUTUS_VAMMAISTEN = "LISAKOULUTUS_VAMMAISTEN";
    public static final String ELEMENT_ID_LISAKOULUTUS_TALOUS = "LISAKOULUTUS_TALOUS";
    public static final String ELEMENT_ID_LISAKOULUTUS_AMMATTISTARTTI = "LISAKOULUTUS_AMMATTISTARTTI";
    public static final String ELEMENT_ID_LISAKOULUTUS_KANSANOPISTO = "LISAKOULUTUS_KANSANOPISTO";
    public static final String ELEMENT_ID_LISAKOULUTUS_MAAHANMUUTTO = "LISAKOULUTUS_MAAHANMUUTTO";
    public static final String ELEMENT_ID_LISAKOULUTUS_MAAHANMUUTTO_LUKIO = "LISAKOULUTUS_MAAHANMUUTTO_LUKIO";
    public static final String ELEMENT_ID_LISAKOULUTUS_VALMA = "LISAKOULUTUS_VALMA";
    public static final String ELEMENT_ID_LISAKOULUTUS_TELMA = "LISAKOULUTUS_TELMA";

    public static final String ELEMENT_ID_OSAAMINEN_YOARVOSANAT_PARAS_KIELI_PITKA = "yo-arvosana-paraskieli-pitka";
    public static final String ELEMENT_ID_OSAAMINEN_YOARVOSANAT_PARAS_KIELI_LYHYT = "yo-arvosana-paraskieli-lyhyt";
    public static final String ELEMENT_ID_OSAAMINEN_YOARVOSANAT_AIDINKIELI = "yo-arvosana-aidinkieli";
    public static final String ELEMENT_ID_OSAAMINEN_YOARVOSANAT_MATEMATIIKKA_PITKA = "yo-arvosana-matematiikka-pitka";
    public static final String ELEMENT_ID_OSAAMINEN_YOARVOSANAT_MATEMATIIKKA_LYHYT = "yo-arvosana-matematiikka-lyhyt";
    public static final String ELEMENT_ID_OSAAMINEN_YOARVOSANAT_REAALI = "yo-arvosana-reaali";
    public static final String ELEMENT_ID_OSAAMINEN_YOARVOSANAT_REAALI_RYHMA = "yo-arvosana-reaali-ryhma";

    public static final String PHASE_PERSONAL = "henkilotiedot";
    public static final String PHASE_EDUCATION = "koulutustausta";
    public static final String PHASE_APPLICATION_OPTIONS = "hakutoiveet";
    public static final String PHASE_GRADES = "osaaminen";
    public static final String PHASE_MISC = "lisatiedot";
    public static final String PHASE_PREVIEW = "esikatselu";

    public static final List<String> LANGUAGES = Arrays.asList("fi","sv");
    public static final String YLEINEN_KIELITUTKINTO = "yleinen_kielitutkinto_%s";
    public static final String VALTIONHALLINNON_KIELITUTKINTO = "valtionhallinnon_kielitutkinto_%s";

    public static final String PREFERENCE_ID = "preference%d-Koulutus-id";

    public static final String SUKUPUOLI_MIES = "1";
    public static final String SUKUPUOLI_NAINEN = "2";

    public static final String PREFERENCE_PREFIX = "preference";
    public static final String OPTION_ID_POSTFIX = "-Koulutus-id";
    public static final String PREFERENCE_FRAGMENT_OPTION_ID= "Koulutus-id";

    public static final String PAYMENT_NOTIFICATION_POSTFIX = "_payment_notification_visible";
    public static final String OPTION_GROUP_POSTFIX = "-Koulutus-id-ao-groups";
    public static final String OPTION_ATTACHMENTS_POSTFIX = "-Koulutus-id-attachments";
    public static final String OPTION_ATTACHMENT_GROUP_TYPE = "hakukohde_liiteosoite";
    public static final String PREFERENCE_NAME = "preference%d-Koulutus";
    public static final String PREFERENCE_FRAGMENT_NAME = "Koulutus";
    public static final String PREFERENCE_ORGANIZATION = "preference%d-Opetuspiste";
    public static final String PREFERENCE_ORGANIZATION_ID = "preference%d-Opetuspiste-id";
    public static final String PREFERENCE_FRAGMENT_ORGANIZATION_ID = "Opetuspiste-id";
    public static final String PREFERENCES_VISIBLE = "preferencesVisible";
    public static final String EDUCATION_CODE_KEY = PREFERENCE_ID + "-educationcode";
    public static final String EDUCATION_VOCATIONAL = PREFERENCE_ID + "-vocational";
    public static final String EDUCATION_LANGUAGE = PREFERENCE_ID + "-lang";
    public static final String EDUCATION_LANGUAGE_FI = "FI";
    public static final String EDUCATION_LANGUAGE_SV = "SV";
    public static final String EDUCATION_LANGUAGE_SE = "SE";
    public static final String EDUCATION_LANGUAGE_VE = "VE";
    public static final String EDUCATION_LANGUAGE_OTHER = "XX";
    public static final String EDUCATION_LANGUAGE_EI_SUORITUSTA = "97";
    public static final String EDUCATION_COUNTRY_OTHER = "XXX";

    public static final String PREFERENCE_DISCRETIONARY = "preference%d-discretionary";
    public static final String PREFERENCE_FRAGMENT_DISCRETIONARY = "discretionary";

    public static final String NATIONALITY_CODE_FI = "FIN";
    public static final String ULKOMAINEN_TUTKINTO = "0";
    public static final String PERUSKOULU = "1";
    public static final String YLIOPPILAS = "9";
    public static final String OSITTAIN_YKSILOLLISTETTY = "2";
    public static final String ALUEITTAIN_YKSILOLLISTETTY = "3";
    public static final String YKSILOLLISTETTY = "6";
    public static final String KESKEYTYNYT = "7";

    public static final String YLIOPPILASTUTKINTO = "ylioppilastutkinto";
    public static final String YLIOPPILASTUTKINTO_FI = "fi";
    public static final String LUKIO_PAATTOTODISTUS_VUOSI = "lukioPaattotodistusVuosi";
    public static final String LUKIO_KIELI = "lukion_kieli";

    public static final String PERUSOPETUS_KIELI = "perusopetuksen_kieli";
    public static final String PERUSOPETUS_PAATTOTODISTUSVUOSI = "PK_PAATTOTODISTUSVUOSI";
    public static final String KYMPPI_PAATTOTODISTUSVUOSI = "KYMPPI_PAATTOTODISTUSVUOSI";
    public static final String TOISEN_ASTEEN_SUORITUS = "toisen_asteen_suoritus";
    public static final String TOISEN_ASTEEN_SUORITUSMAA = "toisen_asteen_suoritusmaa";

    public static final String EDUCATION_CODE_MUSIIKKI = "koulutus_321204";
    public static final String EDUCATION_CODE_TANSSI = "koulutus_321501";

    public static final String EDUCATION_CODE_LIIKUNTA = "koulutus_381203";

    public static final String HAKUKAUSI_SYKSY = "kausi_s";
    public static final String HAKUKAUSI_KEVAT = "kausi_k";

    public static final String KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA = "haunkohdejoukko_17";
    public static final String KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN = "haunkohdejoukko_20";
    public static final String KOHDEJOUKKO_KORKEAKOULU = "haunkohdejoukko_12";
    public static final String KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO = "haunkohdejoukko_11";
    public static final String KOHDEJOUKKO_AMMATILLINEN_ERITYISOPETYKSENA = "haunkohdejoukko_15";

    public static final Set<String> TOISEN_ASTEEN_HAKUJEN_KOHDEJOUKOT = Collections.unmodifiableSet(Sets.newHashSet(
            KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO,
            KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA,
            KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN
    ));

    public static final String KOHDEJOUKON_TARKENNE_SIIRTOHAKU = "haunkohdejoukontarkenne_1";
    public static final String KOHDEJOUKON_TARKENNE_AMK_OPE = "haunkohdejoukontarkenne_2";
    public static final String KOHDEJOUKON_TARKENNE_AMK_OPO = "haunkohdejoukontarkenne_5";
    public static final String KOHDEJOUKON_TARKENNE_AMK_ERKKA = "haunkohdejoukontarkenne_4";

    public static final String HAKUTAPA_YHTEISHAKU = "hakutapa_01";
    public static final String HAKUTAPA_ERILLISHAKU = "hakutapa_02";
    public static final String HAKUTAPA_JATKUVA_HAKU = "hakutapa_03";

    public static final String HAKUTYYPPI_VARSINAINEN_HAKU = "hakutyyppi_01";
    public static final String HAKUTYYPPI_TAYDENNYS = "hakutyyppi_02";
    public static final String HAKUTYYPPI_LISAHAKU = "hakutyyppi_03";

    public static final String TUTKINTO_MUU = "399999";
    public static final String OPPILAITOS_TUNTEMATON = "1.2.246.562.10.57118763579";
    public static final String ROOT_ORGANIZATION_OID = "1.2.246.562.10.00000000001";

    public static final Set<String> APPLICATION_BLACKLISTED_FIELDS = new HashSet<>(asList(new String[]{
            "vastauksetMerged", "overriddenAnswers"
    }));

}
