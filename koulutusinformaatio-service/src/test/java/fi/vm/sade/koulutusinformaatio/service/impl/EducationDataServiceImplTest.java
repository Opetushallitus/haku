package fi.vm.sade.koulutusinformaatio.service.impl;

import com.mongodb.DBCollection;
import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.ParentLearningOpportunityDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunityEntity;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ChildLearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.LearningOpportunityData;
import fi.vm.sade.koulutusinformaatio.domain.ParentLearningOpportunity;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author Mikko Majapuro
 */
public class EducationDataServiceImplTest {

    private EducationDataServiceImpl service;
    private ParentLearningOpportunityDAO parentLearningOpportunityDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private DBCollection ploCollection;
    private DBCollection aoCollection;

    @Before
    public void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        parentLearningOpportunityDAO = mock(ParentLearningOpportunityDAO.class);
        ploCollection = mock(DBCollection.class);
        when(parentLearningOpportunityDAO.getCollection()).thenReturn(ploCollection);
        applicationOptionDAO = mock(ApplicationOptionDAO.class);
        aoCollection = mock(DBCollection.class);
        when(applicationOptionDAO.getCollection()).thenReturn(aoCollection);
        service = new EducationDataServiceImpl(parentLearningOpportunityDAO, applicationOptionDAO, modelMapper);
    }

    @Test
    public void testSave() {
        LearningOpportunityData learningOpportunityData = new LearningOpportunityData();
        List<ParentLearningOpportunity> parentLearningOpportunities = new ArrayList<ParentLearningOpportunity>();
        List<ApplicationOption> applicationOptions = new ArrayList<ApplicationOption>();
        ApplicationOption ao = new ApplicationOption();
        ao.setId("3.3.3");
        applicationOptions.add(ao);
        ParentLearningOpportunity plo = new ParentLearningOpportunity();
        plo.setId("1.2.3");
        plo.setApplicationOptions(applicationOptions);
        ChildLearningOpportunity clo = new ChildLearningOpportunity();
        clo.setId("2.2.2");
        clo.setApplicationOptions(applicationOptions);
        List<ChildLearningOpportunity> children = new ArrayList<ChildLearningOpportunity>();
        children.add(clo);
        plo.setChildren(children);
        parentLearningOpportunities.add(plo);
        learningOpportunityData.setApplicationOptions(applicationOptions);
        learningOpportunityData.setParentLearningOpportinities(parentLearningOpportunities);

        service.save(learningOpportunityData);
        verify(ploCollection, times(1)).drop();
        verify(aoCollection, times(1)).drop();
        verify(parentLearningOpportunityDAO, times(1)).save(any(ParentLearningOpportunityEntity.class));
        verify(applicationOptionDAO, times(1)).save(any(ApplicationOptionEntity.class));
    }
}
