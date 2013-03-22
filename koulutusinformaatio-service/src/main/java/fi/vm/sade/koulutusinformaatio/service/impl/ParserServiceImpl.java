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

package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.domain.ChildLearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.LearningOpportunityData;
import fi.vm.sade.koulutusinformaatio.domain.ParentLearningOpportunity;
import fi.vm.sade.koulutusinformaatio.service.ParserService;
import fi.vm.sade.tarjonta.publication.types.*;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@Service
public class ParserServiceImpl implements ParserService {

    @Override
    public LearningOpportunityData parse(Source source) throws JAXBException {
        LearningOpportunityData learningOpportunityData = new LearningOpportunityData();

        Unmarshaller unmashaller = JAXBContext.newInstance(LearningOpportunityDownloadDataType.class.getPackage().getName()).createUnmarshaller();
        LearningOpportunityDownloadDataType downloadData = (LearningOpportunityDownloadDataType)unmashaller.unmarshal(source);;
        List<LearningOpportunityInstanceType> loiList = downloadData.getLearningOpportunityInstance();

        // temporary map that holds child
        Map<String, ChildLearningOpportunity> children = new HashMap<String, ChildLearningOpportunity>();

        List<ParentLearningOpportunity> parents = new ArrayList<ParentLearningOpportunity>();

        for (LearningOpportunityInstanceType loi : loiList) {
            ChildLearningOpportunity newChild = parseLearningOpportunityChild(loi);
            children.put(newChild.getId(), newChild);
        }

        List<LearningOpportunitySpecificationType> losList = downloadData.getLearningOpportunitySpecification();
        for (LearningOpportunitySpecificationType los : losList) {
            if (los.getChildLOSRefs() != null && !los.getChildLOSRefs().isEmpty()) {
                parents.add(parseLearningOpportunityParent(los, children));
            }
        }



        learningOpportunityData.setParentLearningOpportinities(parents);

        return learningOpportunityData;
    }

    private ParentLearningOpportunity parseLearningOpportunityParent(LearningOpportunitySpecificationType los, Map<String, ChildLearningOpportunity> children) {
        ParentLearningOpportunity parent = new ParentLearningOpportunity();
        parent.setId(los.getId());
        parent.setName(resolveFinnishText(los.getName()));
        List<ChildLearningOpportunity> childList = new ArrayList<ChildLearningOpportunity>();
        for (LearningOpportunitySpecificationRefType ref : los.getChildLOSRefs()) {
            LearningOpportunitySpecificationType child = (LearningOpportunitySpecificationType) ref.getRef();
            childList.add(children.get(child.getId()));
        }
        parent.setChildren(childList);
        return parent;
    }

    private ChildLearningOpportunity parseLearningOpportunityChild(LearningOpportunityInstanceType loi) {

        LearningOpportunitySpecificationType los = (LearningOpportunitySpecificationType) loi.getSpecificationRef().getRef();

        ChildLearningOpportunity child = new ChildLearningOpportunity(los.getId(), resolveFinnishText(los.getName()));

        return child;
    }

    private String resolveFinnishText(List<ExtendedStringType> strings) {
        for (ExtendedStringType string : strings) {
            if (string.getLang().equals("fi")) {
                return string.getValue();
            }
        }
        return "TEXT NOT FOUND";
    }

}
