package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.ParentLearningOpportunityDAO;
import fi.vm.sade.koulutusinformaatio.service.EducationDataService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mikko Majapuro
 */
@Service
public class EducationDataServiceImpl implements EducationDataService {

    private ParentLearningOpportunityDAO parentLearningOpportunityDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private ModelMapper modelMapper;

    @Autowired
    public EducationDataServiceImpl(ParentLearningOpportunityDAO parentLearningOpportunityDAO,
                                    ApplicationOptionDAO applicationOptionDAO, ModelMapper modelMapper) {
        this.parentLearningOpportunityDAO = parentLearningOpportunityDAO;
        this.applicationOptionDAO = applicationOptionDAO;
        this.modelMapper = modelMapper;
    }
}
