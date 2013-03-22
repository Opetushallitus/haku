package fi.vm.sade.koulutusinformaatio.dao.entity;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Entity("learningOpportunities")
public class ParentLearningOpportunityEntity {

    @Id
    private String id;
    private String name;
    @Embedded
    private List<ChildLearningOpportunityEntity> childLearningOpportunityEntities;

    public ParentLearningOpportunityEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChildLearningOpportunityEntity> getChildLearningOpportunityEntities() {
        return childLearningOpportunityEntities;
    }

    public void setChildLearningOpportunityEntities(List<ChildLearningOpportunityEntity> childLearningOpportunityEntities) {
        this.childLearningOpportunityEntities = childLearningOpportunityEntities;
    }
}
