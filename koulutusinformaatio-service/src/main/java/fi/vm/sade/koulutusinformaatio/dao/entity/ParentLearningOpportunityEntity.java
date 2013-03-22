package fi.vm.sade.koulutusinformaatio.dao.entity;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

/**
 * @author Mikko Majapuro
 */
@Entity("learningOpportunities")
public class ParentLearningOpportunityEntity {

    public ParentLearningOpportunityEntity() {

    }

    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
