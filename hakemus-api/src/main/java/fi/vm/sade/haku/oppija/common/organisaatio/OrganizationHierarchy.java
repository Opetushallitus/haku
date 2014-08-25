package fi.vm.sade.haku.oppija.common.organisaatio;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrganizationHierarchy {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationHierarchy.class);

    private final OrganizationService organizationService;

    public OrganizationHierarchy(OrganizationService organizationService){
        this.organizationService= organizationService;
    }

    private HashMap<String, OrganizationEntry> organizations = new HashMap<String, OrganizationEntry>();

    public void addOrganization(String id){
        LOGGER.debug("Adding " + id + " to hierarchty");
        _getOrganization(id);
    }

    public Set<Organization> getAllSubOrganizations(final String id){
        LOGGER.debug("Getting sub organization hierarchy for" + id);
        final HashSet<Organization> organizationSet = new HashSet<Organization>();
        _getAllSubOrganizations(id, organizationSet);
        LOGGER.debug("Returning " + organizationSet.size() + " sub organizations");
        return organizationSet;
    }

    private void _getAllSubOrganizations(final String ofId, final Set<Organization> toOrganizationSet){
        LOGGER.debug("Sub organizations in hierarchy for " + ofId);
        final OrganizationEntry organizationEntry = _getOrganization(ofId);
        toOrganizationSet.add(organizationEntry.getOrganization());

        for (OrganizationEntry childOrganizationEntry : organizationEntry.getChildOrganizations()){
            _getAllSubOrganizations(childOrganizationEntry.getOrganization().getOid(), toOrganizationSet);
        }
    }

    private OrganizationEntry _getOrganization(final String id){
        LOGGER.debug("Getting organization entry for " + id);
        if (organizations.containsKey(id))
            return organizations.get(id);
        LOGGER.debug("Organization " + id + " not in hiearchy. Adding.");
        final Organization org = organizationService.findByOppilaitosnumero(ImmutableList.of(id)).get(0);
        final String parentOid = org.getParentOid();
        LOGGER.debug("Organization " + id + " parent is " + parentOid);
        final OrganizationEntry parent = parentOid == null ? null: _getOrganization(parentOid);
        final OrganizationEntry organizationEntry = new OrganizationEntry(org, parent);
        if (null != parent){
            LOGGER.debug("Attaching " + id + " to parent " + parent.getOrganization().getOid());
            parent.addChildOrganization(organizationEntry);
        }
        organizations.put(organizationEntry.getOrganization().getOid(), organizationEntry);
        return organizationEntry;
    }

    private final class OrganizationEntry{
        final private Organization organization;

        final private OrganizationEntry parent;
        final private HashSet<OrganizationEntry> children = new HashSet<OrganizationEntry>();

        public OrganizationEntry(Organization organization,OrganizationEntry parent) {
            this.organization = organization;
            this.parent = parent;
        }

        public Organization getOrganization() {
            return organization;
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

            if (!organization.equals(that.organization)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return  29 * organization.hashCode();
        }
    }
}
