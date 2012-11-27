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

package fi.vm.sade.oppija.tarjonta.service.generator;

import org.apache.solr.common.SolrInputDocument;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: majapuro
 * Date: 10/23/12
 * Time: 12:48 PM
 */
public final class DummyDataGenerator {

    private static String[] losNames = {"Ensihoidon koulutusohjelma", "Tietotekniikan koulutusohjelma",
            "Liikunnanohjauksen koulutusohjelma", "Rakennustekniikan koulutusohjelma",
            "Kansantaloustieteiden koulutusohjelma",
            "Fysikaalisten tieteiden koulutusohjelma", "Geologian koulutusohjelma", "Matematiikan koulutusohjelma"};
    private static String[] degreeTitles = {"sosiaali- ja terveysalan pt", "liikunnanohjauksen perustutkinto",
            "tietotekniikan maisteritutkinto", "kauppatieteiden maisteri", "arkkitehti",
            "metsätalousinsinööri", "rakennusmestari"};
    private static String[] qualificationTitles = {"lähihoitaja", "diplomi-insinööri",
            "liikunnanohjaaja", "maisteri", "tradenomi", "datanomi"};
    private static String[] educationDomains = {"sosiaali- terveys- ja liikunta-ala", "it-ala", "elintarvikeala",
            "Rakennus- ja kiinteistöala", "kulttuuriala", "luonnonvara- ja ympäristöala"};
    private static String[] institutionNames = {
            "Helsingin sosiaali- ja terveysalan oppilaitos, Laakson koulutusyksikkö",
            "Tampereen teknillinen yliopisto", "Tampereen yliopisto", "Helsingin kauppakorkeakoulu, Kallion yksikkö",
            "Tampereen ammattikorkeakoulu"};

    private DummyDataGenerator() {
    }

    public static Collection<SolrInputDocument> generate() {
        Collection<SolrInputDocument> solrDocuments = new ArrayList<SolrInputDocument>();

        Random random = new Random();
        for (int i = 1; i < 100; ++i) {
            SolrInputDocument solrDocument = new SolrInputDocument();
            solrDocument.addField("AOId", i);
            solrDocument.addField("AOTitle", "Tutkinto " + i);
            solrDocument.addField("LOIPrerequisite", random.nextBoolean() ? "Ylioppilas tai lukion suorittanut" :
                    "Perusopetuksen oppimäärä. Ylioppilaat ja lukion suorittaneet eivät hakukelpoisia.");
            solrDocument.addField("LOILanguagesOfInstruction", random.nextBoolean() ? "fi" : "en");
            solrDocument.addField("LOIFormOfTeaching", "Classroom");
            solrDocument.addField("AOEligibilityRequirements", "peruskoulu/yo-pohjaisiin kohteisiin lukio");
            solrDocument.addField("AODescription", "Lorem Lipsum");
            int score = random.nextInt(50);
            solrDocument.addField("AOLastYearMaxScore", score + random.nextInt(20));
            solrDocument.addField("AOLastYearMinScore", score);
            solrDocument.addField("AOLastYearTotalApplicants", random.nextInt(1500));
            solrDocument.addField("AOStartingQuota", random.nextInt(200));
            solrDocument.addField("AOSelectionCriteriaDescription",
                    "Perusopetuksen oppimäärän suorittaneiden opiskelijaksi ottaminen. " +
                            "Yhteishaun lisäksi hakija voi halutessaan täyttää liikunnan alakohtaisen" +
                            "lisätietolomakkeen,jolla voi saada 1–3 lisäpistettä.");
            generateLearningOpportunitySpecification(solrDocument, random);
            generateLearningOpportunityProvider(solrDocument, random);
            generateEntranceExamination(solrDocument, random);

            solrDocument.addField("formPath", "test/yhteishaku");
            solrDocument.addField("tmpAOApplyAdditionalInfo", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                    "Aliquam tortor nisi, egestas id pellentesque ac, scelerisque in tortor. Morbi accumsan libero erat. " +
                    "Quisque nisl erat, fringilla quis ullamcorper vel, viverra eu leo. Nulla facilisi. Fusce a leo id " +
                    "tellus molestie imperdiet vel ut augue. Suspendisse interdum malesuada iaculis. Sed et urna ante, " +
                    "id varius ipsum. Fusce imperdiet sapien convallis purus mattis euismod. Quisque et metus sit amet " +
                    "nulla pharetra consequat at vel tellus. Proin vulputate eros at quam rutrum id dignissim magna dictum. ");
            solrDocument.addField("tmpLOSEducationField", "Opintojesi aikana erikoistut joko markkinointiin, laskentaan" +
                    " ja rahoitukseen tai työyhteisön kehittämiseen.");
            Calendar cal = GregorianCalendar.getInstance();
            solrDocument.addField("tmpASStart", cal.getTime());
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH + 1));
            solrDocument.addField("tmpASEnd", cal.getTime());
            solrDocument.addField("tmpAOLastYearQualified", 45);
            solrDocuments.add(solrDocument);
        }
        return solrDocuments;
    }

    private static void generateLearningOpportunitySpecification(SolrInputDocument solrDocument, Random random) {
        solrDocument.addField("LOSName", losNames[random.nextInt(losNames.length)]);
        solrDocument.addField("LOSType", "DegreeProgramme");
        solrDocument.addField("LOSCredits", random.nextInt(250));
        solrDocument.addField("LOSCreditsUnit", "CreditUnits");
        solrDocument.addField("LOSDegreeTitle", degreeTitles[random.nextInt(degreeTitles.length)]);
        solrDocument.addField("LOSQualification", qualificationTitles[random.nextInt(qualificationTitles.length)]);
        solrDocument.addField("LOSDescriptionStructureDiagram", "Lorem ipsum dolor sit amet, consectetur adipisicing " +
                "elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        solrDocument.addField("LOSDescriptionAccessToFurtherStudies",
                "Ammatillinen perustutkinto antaa yleisen korkeakoulukelpoisuuden.");
        solrDocument.addField("LOSDescriptionEducationAndProfessionalGoals",
                "Lorem ipsum dolor sit amet, consectetur adipisicing elit.");
        solrDocument.addField("LOSEducationDomain", educationDomains[random.nextInt(educationDomains.length)]);
        solrDocument.addField("LOSEducationDegree", "Ammatillinen koulutus");
        solrDocument.addField("LOSStydyDomain", educationDomains[random.nextInt(educationDomains.length)]);
    }

    private static void generateLearningOpportunityProvider(SolrInputDocument solrDocument, Random random) {
        solrDocument.addField("LOPInstitutionInfoName", institutionNames[random.nextInt(institutionNames.length)]);
        solrDocument.addField("LOPInstitutionInfoGeneralDescription",
                "Lorem ipsum dolor sit amet, consectetur adipisicing elit.");
    }

    private static void generateEntranceExamination(SolrInputDocument solrDocument, Random random) {
        solrDocument.addField("AOExaminationDescription", "Valintakoe on yksipäiväinen, maksullinen.");
        solrDocument.addField("AOExaminationTitle", "Pääsy- ja soveltuvuuskoe");
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH + 1));
        solrDocument.addField("AOExaminationStart", cal);
        cal.set(Calendar.HOUR, cal.get(Calendar.HOUR + 3));
        solrDocument.addField("AOExaminationEnd", cal.getTime());
        solrDocument.addField("AOExaminationLocation", "Aalto Auditorio, Mannerheimintie 3 00100 Helsinki");
    }
}
