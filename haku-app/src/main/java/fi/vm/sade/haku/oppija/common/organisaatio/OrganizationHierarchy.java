package fi.vm.sade.haku.oppija.common.organisaatio;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OrganizationHierarchy {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationHierarchy.class);

    private final OrganizationService organizationService;

    public OrganizationHierarchy(OrganizationService organizationService){
        this.organizationService= organizationService;
    }

    private HashMap<String, OrganizationEntry> organizations = new HashMap<String, OrganizationEntry>();

    public void addOrganization(String id){

        LOGGER.debug("Adding " + id + " to hierarchty");
        getOrganization(id);
    }

    public List<Map<String, Object>> getAllSubOrganizations(String id){
        LOGGER.debug("Sub organizations in hierarchy for " + id);
        OrganizationEntry organization = getOrganization(id);
        HashSet<Map<String, Object>> organizationSet = new HashSet<Map<String,Object>>();
        for (OrganizationEntry child : organization.getChildOrganizations()){
            organizationSet.addAll(getAllSubOrganizations(child.getId()));
        }
        Map<String, Object> entry = new ImmutableMap.Builder<String,Object>().put("id", organization.getId()).put("name", organization.getName()).build();
        organizationSet.add(entry);
        LOGGER.debug("Returning " + organizationSet.size() + " sub organizations");
        return new ArrayList<Map<String, Object>>(organizationSet);
    }

    private OrganizationEntry getOrganization(String id){
        LOGGER.debug("Getting organization entry for " + id);
        if (organizations.containsKey(id))
            return organizations.get(id);
        LOGGER.debug("Organization " + id + " not in hiearchy. Adding.");
        Organization org = organizationService.findByOppilaitosnumero(ImmutableList.of(id)).get(0);
        String parentOid = org.getParentOid();
        LOGGER.debug("Organization " + id + " parent is " + parentOid);
        OrganizationEntry parent = parentOid == null? null: getOrganization(parentOid);
        OrganizationEntry organizationEntry = new OrganizationEntry(org.getOid(), parent, org.getName());
        if (null != parent){
            LOGGER.debug("Attaching " + id + " to parent " + parent.getId());
            parent.addChildOrganization(organizationEntry);
        }
        organizations.put(organizationEntry.getId(), organizationEntry);
        return organizationEntry;
    }

    private class OrganizationEntry{
        private String id;
        private I18nText name;
        private OrganizationEntry parent;
        private HashSet<OrganizationEntry> children = new HashSet<OrganizationEntry>();

        public OrganizationEntry(String id,OrganizationEntry parent, I18nText name) {
            this.id = id;
            this.name = name;
            this.parent = parent;
        }

        public String getId() {
            return id;
        }

        public I18nText getName() {
            return name;
        }

        public OrganizationEntry getParent() {
            return parent;
        }

        public List<OrganizationEntry> getChildOrganizations(){
            return ImmutableList.copyOf(children);
        }

        public void addChildOrganization(OrganizationEntry childOrganization){
            children.add(childOrganization);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            OrganizationEntry that = (OrganizationEntry) o;

            if (!id.equals(that.id)) {
                return false;
            }
            if (!name.equals(that.name)) {
                return false;
            }
            if (parent != null ? !parent.equals(that.parent) : that.parent != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = 31 * result + name.hashCode();
            result = 31 * result + (parent != null ? parent.hashCode() : 0);
            return result;
        }
    }
}
