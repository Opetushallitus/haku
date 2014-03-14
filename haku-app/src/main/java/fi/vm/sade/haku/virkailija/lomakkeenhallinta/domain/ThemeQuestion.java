package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;

import java.util.Map;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public abstract class ThemeQuestion implements ConfiguredElement {

    public enum Theme {
        HENKILOTIEDOT, KOULUTUSTAUSTA, HAKUTOIVEET, ARVOSANAT, KIELITAITO, TYOKOKEMUS, MOTIVAATIO, LIITTEET, LUPATIEDOT
    }

    public enum Type {
        TEXT
    }

    @JsonProperty(value = "_id")
    // ThemeQuestion oid
    protected org.bson.types.ObjectId id;

    //Application System oid
    protected String applicationSystemId;

    // Where the question is to be displayed
    protected Theme theme;

    //user ident oid
    protected String creatorOid;

    // organization oid
    protected String ownerOrganization;

    // Type of question
    protected Type type;

    // Parameters for the question
    protected Map<String, String> parameters;

    // Attached to learning opportunity Identifier
    protected String learningOpportunityProviderId;

    @JsonCreator
    protected ThemeQuestion() {
        this.parameters = new HashMap<String, String>();
    }

    protected ThemeQuestion(String applicationSystemId, Theme theme, String creatorPersonOid, String ownerOrganizationOid, Type type, Map<String, String> parameters, String learningOpportunityProviderId) {
        this.applicationSystemId =  applicationSystemId;
        this.theme = theme;
        this.creatorPersonOid = creatorPersonOid;
        this.ownerOrganizationOid = ownerOrganizationOid;
        this.type = type;
        this.parameters = new HashMap<String, String>(parameters);
        this.learningOpportunityProviderId = learningOpportunityProviderId;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getApplicationSystem() {
        return applicationSystemId;
    }
    
    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public String getCreatorPersonOid() {
        return creatorPersonOid;
    }

    public void setCreatorPersonOid(String creatorPersonOid) {
        this.creatorPersonOid = creatorPersonOid;
    }

    public String getOwnerOrganizationOid() {
        return ownerOrganizationOid;
    }

    public void setOwnerOrganizationOid(String ownerOrganizationOid) {
        this.ownerOrganizationOid = ownerOrganizationOid;
    }

    public Type getType() {
        return type;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getLearningOpportunityProviderId(){
        return learningOpportunityProviderId;
    }


    // Try to remove these
    public void setParameters(Map<String, String> parameters) {
        this.parameters = new HashMap<String, String>(parameters);
    }
    
    public void setType(Type type) {
        this.type = type;
    }
}
