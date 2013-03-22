package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityParentDAO;
import fi.vm.sade.koulutusinformaatio.service.EducationDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mikko Majapuro
 */
@Service
public class EducationDataServiceImpl implements EducationDataService {

    private LearningOpportunityParentDAO learningOpportunityParentDAO;
    private ApplicationOptionDAO applicationOptionDAO;

    @Autowired
    public EducationDataServiceImpl(LearningOpportunityParentDAO learningOpportunityParentDAO,
                                    ApplicationOptionDAO applicationOptionDAO) {
        this.learningOpportunityParentDAO = learningOpportunityParentDAO;
        this.applicationOptionDAO = applicationOptionDAO;
    }
}
