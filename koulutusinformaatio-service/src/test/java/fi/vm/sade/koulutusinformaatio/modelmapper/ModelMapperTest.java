package fi.vm.sade.koulutusinformaatio.modelmapper;

import fi.vm.sade.koulutusinformaatio.dao.entity.ChildLearningOpportunityEntity;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunityEntity;
import fi.vm.sade.koulutusinformaatio.domain.ChildLearningOpportunity;
import fi.vm.sade.koulutusinformaatio.domain.ParentLearningOpportunity;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Mikko Majapuro
 */
public class ModelMapperTest {

    private ModelMapper modelMapper = new ModelMapper();

    @Test
    public void testMapParentLearningOpportunityToEntity() {
        ParentLearningOpportunity parent = new ParentLearningOpportunity();
        parent.setId("123");
        parent.setName("parentName");

        ChildLearningOpportunity child = new ChildLearningOpportunity("111", "child1Name");
        ChildLearningOpportunity child2 = new ChildLearningOpportunity("222", "child2Name");

        List<ChildLearningOpportunity> children = new ArrayList<ChildLearningOpportunity>();
        children.add(child);
        children.add(child2);
        parent.setChildren(children);

        ParentLearningOpportunityEntity entity = modelMapper.map(parent, ParentLearningOpportunityEntity.class);

        assertNotNull(entity);
        assertEquals(parent.getId(), entity.getId());
        assertEquals(parent.getName(), entity.getName());
        assertNotNull(entity.getChildren());
        assertEquals(2, entity.getChildren().size());
        assertEquals(child.getId(), entity.getChildren().get(0).getId());
        assertEquals(child.getName(), entity.getChildren().get(0).getName());
        assertEquals(child2.getId(), entity.getChildren().get(1).getId());
        assertEquals(child2.getName(), entity.getChildren().get(1).getName());
    }

    @Test
    public void testMapParentLearningOpportunityEntityToDomainObject() {
        ParentLearningOpportunityEntity entity = new ParentLearningOpportunityEntity();
        entity.setId("999");
        entity.setName("entityName");

        List<ChildLearningOpportunityEntity> children = new ArrayList<ChildLearningOpportunityEntity>();
        ChildLearningOpportunityEntity child = new ChildLearningOpportunityEntity();
        child.setId("444");
        child.setName("child1EntityName");
        children.add(child);
        ChildLearningOpportunityEntity child2 = new ChildLearningOpportunityEntity();
        child2.setId("555");
        child2.setName("child2EntityName");
        children.add(child2);

        entity.setChildren(children);

        ParentLearningOpportunity domain = modelMapper.map(entity, ParentLearningOpportunity.class);
        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getName(), domain.getName());
        assertNotNull(domain.getChildren());
        assertEquals(2, domain.getChildren().size());
        assertEquals(child.getId(), domain.getChildren().get(0).getId());
        assertEquals(child.getName(), domain.getChildren().get(0).getName());
        assertEquals(child2.getId(), domain.getChildren().get(1).getId());
        assertEquals(child2.getName(), domain.getChildren().get(1).getName());
    }
}
