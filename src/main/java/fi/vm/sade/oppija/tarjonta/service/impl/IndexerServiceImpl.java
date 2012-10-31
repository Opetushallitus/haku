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

package fi.vm.sade.oppija.tarjonta.service.impl;


import fi.vm.sade.oppija.tarjonta.client.TarjontaClient;
import fi.vm.sade.oppija.tarjonta.service.IndexService;
import fi.vm.sade.oppija.tarjonta.service.generator.DummyDataGenerator;
import fi.vm.sade.tarjonta.publication.types.*;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Service
public class IndexerServiceImpl implements IndexService {

    public static final Logger LOGGER = LoggerFactory.getLogger(IndexerServiceImpl.class);

    private final HttpSolrServer httpSolrServer;

    private final TarjontaClient tarjontaClient;

    @Autowired
    public IndexerServiceImpl(HttpSolrServer httpSolrServer, TarjontaClient tarjontaClient) {
        this.httpSolrServer = httpSolrServer;
        this.tarjontaClient = tarjontaClient;
    }

    @Override
    public boolean update(final URL url) {

        try {
            Collection<SolrInputDocument> documents = parseDocuments(url);
            httpSolrServer.add(documents);
            httpSolrServer.commit();
        } catch (Exception e) {
            LOGGER.error("Indeksin päivitys epäonnistui ", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean generate() {
        try {
            Collection<SolrInputDocument> documents = DummyDataGenerator.generate();
            httpSolrServer.add(documents);
            httpSolrServer.commit();
        } catch (Exception e) {
            LOGGER.error("Indeksin päivitys epäonnistui");
            return false;
        }
        return true;
    }

    public boolean drop() {
        boolean dropped = false;
        try {
            httpSolrServer.deleteByQuery("*:*");
            dropped = true;
        } catch (Exception e) {
            LOGGER.error("drop failed", e);
        }
        return dropped;
    }

    private static LearningOpportunityDownloadDataType getLearningOpportunityDownloadDataType(final String data) throws JAXBException, MalformedURLException {
        JAXBElement<LearningOpportunityDownloadDataType> learningOpportunityDataTypeJAXBElement =
                (JAXBElement<LearningOpportunityDownloadDataType>) getUnmarshaller().unmarshal(new StreamSource(new StringReader(data)));
        return learningOpportunityDataTypeJAXBElement.getValue();
    }

    private static LearningOpportunityDownloadDataType getLearningOpportunityDownloadDataType(final URL url) throws JAXBException, MalformedURLException {
        JAXBElement<LearningOpportunityDownloadDataType> learningOpportunityDataTypeJAXBElement =
                (JAXBElement<LearningOpportunityDownloadDataType>) getUnmarshaller().unmarshal(url);
        return learningOpportunityDataTypeJAXBElement.getValue();
    }

    private static Unmarshaller getUnmarshaller() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(LearningOpportunityDownloadDataType.class.getPackage().getName());
        return jaxbContext.createUnmarshaller();
    }

    public Collection<SolrInputDocument> parseDocuments(final URL url) throws JAXBException, MalformedURLException {
        LearningOpportunityDownloadDataType learningOpportunityDownloadDataType = getLearningOpportunityDownloadDataType(url);

        Collection<SolrInputDocument> solrDocuments = new ArrayList<SolrInputDocument>();
        for (ApplicationOptionType applicationOptionType : learningOpportunityDownloadDataType.getApplicationOption()) {
            SolrInputDocument solrDocument = new SolrInputDocument();
            addApplicationOption(solrDocument, applicationOptionType);
            ApplicationOptionType.LearningOpportunities learningOpportunities = applicationOptionType.getLearningOpportunities();
            List<LearningOpportunityInstanceRefType> instanceRef = learningOpportunities.getInstanceRef();
            for (LearningOpportunityInstanceRefType learningOpportunityInstanceRefType : instanceRef) {
                LearningOpportunityInstanceType ref = (LearningOpportunityInstanceType) learningOpportunityInstanceRefType.getRef();
                LearningOpportunitySpecificationType ref1 = (LearningOpportunitySpecificationType) ref.getSpecificationRef().getRef();

                addLearningOpportunityProviderType(solrDocument, ref1.getOfferedBy());
                addLearningOpportunitySpecification(solrDocument, ref1);
                addLearningOpportunityInstance(solrDocument, ref);
            }
            // tmp dev fields
            solrDocument.addField("tmpAOApplyAdditionalInfo", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam tortor nisi, egestas id pellentesque ac, scelerisque in tortor. Morbi accumsan libero erat. Quisque nisl erat, fringilla quis ullamcorper vel, viverra eu leo. Nulla facilisi. Fusce a leo id tellus molestie imperdiet vel ut augue. Suspendisse interdum malesuada iaculis. Sed et urna ante, id varius ipsum. Fusce imperdiet sapien convallis purus mattis euismod. Quisque et metus sit amet nulla pharetra consequat at vel tellus. Proin vulputate eros at quam rutrum id dignissim magna dictum. ");
            solrDocument.addField("tmpLOSEducationField", "Opintojesi aikana erikoistut joko markkinointiin, laskentaan ja rahoitukseen tai työyhteisön kehittämiseen.");
            Calendar cal = GregorianCalendar.getInstance();
            solrDocument.addField("tmpASStart", cal.getTime());
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH + 1));
            solrDocument.addField("tmpASEnd", cal.getTime());
            solrDocument.addField("tmpAOLastYearQualified", 45);
            solrDocument.addField("tmpHakuId", "test");
            solrDocument.addField("tmpLomakeId", "yhteishaku");

            solrDocuments.add(solrDocument);
        }

        return solrDocuments;
    }

    private void addLearningOpportunityProviderType(SolrInputDocument solrDocument, LearningOpportunityProviderRefType learningOpportunityProviderType) {
        if (learningOpportunityProviderType != null) {
            LearningOpportunityProviderType learningOpportunityProvider = (LearningOpportunityProviderType) learningOpportunityProviderType.getRef();
            solrDocument.addField("LOPId", learningOpportunityProvider.getId());

            //LearningOpportunityProviderType.GeneralInformation generalInformation = learningOpportunityProvider.getGeneralInformation();
            LearningOpportunityProviderType.InstitutionInfo institutionInfo = learningOpportunityProvider.getInstitutionInfo();
            //opetuspiste
            solrDocument.addField("LOPInstitutionInfoName", getValueOfExtendedString(institutionInfo.getName()));
            solrDocument.addField("LOPInstitutionInfoGeneralDescription", getValueOfTextType(institutionInfo.getGeneralDescription()));
        }
    }

    private void addApplicationOption(SolrInputDocument solrDocument, ApplicationOptionType applicationOptionType) {
        solrDocument.addField("AOId", applicationOptionType.getIdentifier().getValue());
//      solrDocument.addField("AODescription", getValueOfExtendedString(applicationOptionType.getDescription()));
        solrDocument.addField("AOTitle", getValueOfExtendedString(applicationOptionType.getTitle()));
        solrDocument.addField("AOEligibilityRequirements", getValueOfExtendedString(applicationOptionType.getEligibilityRequirements().getDescription()));
        solrDocument.addField("AODescription", getValueOfExtendedString(applicationOptionType.getSelectionCriterions().getDescription()));
        solrDocument.addField("AOLastYearMaxScore", applicationOptionType.getSelectionCriterions().getLastYearMaxScore());
        solrDocument.addField("AOLastYearMinScore", applicationOptionType.getSelectionCriterions().getLastYearMinScore());
        solrDocument.addField("AOLastYearTotalApplicants", applicationOptionType.getSelectionCriterions().getLastYearTotalApplicants());
        solrDocument.addField("AOStartingQuota", applicationOptionType.getSelectionCriterions().getStartingQuota());
        solrDocument.addField("AOSelectionCriteriaDescription", getValueOfExtendedString(applicationOptionType.getSelectionCriterions().getDescription()));
        List<AttachmentCollectionType.Attachment> attachments = applicationOptionType.getSelectionCriterions().getAttachments().getAttachment();
        for (AttachmentCollectionType.Attachment attachment : attachments) {
            solrDocument.addField("AOAttachmentDescription", getValueOfExtendedString(attachment.getDescription().getText()));
            solrDocument.addField("AOAttachmentType", attachment.getType().getValue());
            solrDocument.addField("AOAttachmentReturnDueDate", attachment.getReturn().getDueDate());
            solrDocument.addField("AOAttachmentReturnTo", attachment.getReturn().getTo());
        }
        List<SelectionCriterionsType.EntranceExaminations.Examination> examinations = applicationOptionType.getSelectionCriterions().getEntranceExaminations().getExamination();
        for (SelectionCriterionsType.EntranceExaminations.Examination examination : examinations) {
            solrDocument.addField("AOExaminationDescription", getValueOfExtendedString(examination.getDescription()));
            solrDocument.addField("AOExaminationTitle", getValueOfExtendedString(examination.getExaminationType().getTitle()));
            List<ExaminationEventType> examinationEvents = examination.getExaminationEvent();
            for (ExaminationEventType examinationEvent : examinationEvents) {
                solrDocument.addField("AOExaminationStart", examinationEvent.getStart());
                solrDocument.addField("AOExaminationStartDate", examinationEvent.getStart());
                solrDocument.addField("AOExaminationEnd", examinationEvent.getEnd());
                List<ExaminationLocationType> locations = examinationEvent.getLocations().getLocation();
                for (ExaminationLocationType location : locations) {
                    solrDocument.addField("AOExaminationLocationName", location.getName());
                    solrDocument.addField("AOExaminationLocationAddressLine", location.getAddressLine());
                    solrDocument.addField("AOExaminationLocationPostalCode", location.getPostalCode());
                    solrDocument.addField("AOExaminationLocationCity", location.getCity());
                }
            }
            solrDocument.addField("AOExaminationDescription", examination.getExaminationEvent());
        }
    }

    private void addLearningOpportunityInstance(SolrInputDocument solrDocument, LearningOpportunityInstanceType ref) {
//        solrDocument.addField("LOIId", ref.getId());
        List<CodeValueCollectionType.Code> languages = ref.getLanguagesOfInstruction().getCode();
        for (CodeValueCollectionType.Code lang : languages) {
            solrDocument.addField("LOILanguagesOfInstruction", lang);
        }

        List<CodeValueCollectionType.Code> formsOfTeaching = ref.getFormsOfTeaching().getCode();
        for (CodeValueCollectionType.Code formOfTeaching : formsOfTeaching) {
            solrDocument.addField("LOIFormOfTeaching", formOfTeaching.getValue());
        }
//        solrDocument.addField("LOIAcademicYear", ref.getAcademicYear());
//        solrDocument.addField("LOIAssessments", getValueOfExtendedString(ref.getAssessments().getAssessment()).trim());
        //solrDocument.addField("LOICostOfEducation", ref.getCostOfEducation().getNoCost());
//        solrDocument.addField("LOIDuration", ref.getDuration());
        //solrDocument.addField("LOIFinalExamination", ref.getFinalExamination());
        //solrDocument.addField("LOIFormOfEducation", ref.getFormOfEducation());
//        solrDocument.addField("LOIKeywords", ref.getKeywords());
        solrDocument.addField("LOIPrerequisite", ref.getPrerequisite().getValue().trim());
//        solrDocument.addField("LOIProfession", ref.getProfession());
//        solrDocument.addField("LOIScholarship", ref.getScholarship());
//        solrDocument.addField("LOIStartDate", ref.getStartDate());

        List<WebLinkCollectionType.Link> links = ref.getWebLinks().getLink();
        for (WebLinkCollectionType.Link link : links) {
            solrDocument.addField("LOIWebLink" + link.getType() + "Uri", link.getUri());
            solrDocument.addField("LOIWebLink" + link.getType() + "Label", getValueOfExtendedString(link.getLabel()));
        }
    }

    private void addLearningOpportunitySpecification(SolrInputDocument solrDocument, LearningOpportunitySpecificationType LOS) {
        solrDocument.addField("LOSId", LOS.getId());
        solrDocument.addField("LOSType", LOS.getType().value());
        solrDocument.addField("LOSName", getValueOfExtendedString(LOS.getName()));
        solrDocument.addField("LOSCredits", LOS.getCredits().getValue());
        solrDocument.addField("LOSCreditsUnit", LOS.getCredits().getUnit());
//        solrDocument.addField("LOSIdentifier", LOS.getId());
        solrDocument.addField("LOSDegreeTitle", LOS.getDegreeTitle().getValue());
        solrDocument.addField("LOSQualification", getValueOfExtendedString(LOS.getQualification().getTitle()));
        solrDocument.addField("LOSDescriptionStructureDiagram", LOS.getDescription().getStructureDiagram());
        solrDocument.addField("LOSDescriptionAccessToFurtherStudies", LOS.getDescription().getAccessToFurtherStudies());
        solrDocument.addField("LOSDescriptionEducationAndProfessionalGoals", LOS.getDescription().getEducationAndProfessionalGoals());
//        solrDocument.addField("LOSDescriptionGeneralDescription", LOS.getDescription().getGeneralDescription());

        LearningOpportunitySpecificationType.Classification classification = LOS.getClassification();
        solrDocument.addField("LOSEducationDomain", classification.getEducationDomain());
        solrDocument.addField("LOSEducationDegree", classification.getEducationDegree());
        solrDocument.addField("LOSStydyDomain", classification.getStudyDomain());
        solrDocument.addField("LOSEducationClassification", classification.getEducationClassification());
    }

    private static String getValueOfExtendedString(List<ExtendedStringType> ref1) {
        return ref1.get(0).getValue();
    }

    private static String getValueOfTextType(List<TextType> ref1) {
        return ref1.get(0).getContent();
    }

}
