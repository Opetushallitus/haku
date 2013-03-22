package fi.vm.sade.koulutusinformaatio.dao.entity;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Id;

/**
 * @author Mikko Majapuro
 */
@Embedded
public class LearningOpportunityChildEntity {

    public LearningOpportunityChildEntity() {

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
