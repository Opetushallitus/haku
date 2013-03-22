package fi.vm.sade.koulutusinformaatio.dao.entity;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Id;

/**
 * @author Mikko Majapuro
 */
@Embedded
public class ChildLearningOpportunityEntity {

    @Id
    private String id;
    private String name;

    public ChildLearningOpportunityEntity() {
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
}
