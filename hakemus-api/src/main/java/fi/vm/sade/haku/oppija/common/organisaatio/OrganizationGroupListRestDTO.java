package fi.vm.sade.haku.oppija.common.organisaatio;

import java.util.ArrayList;
import java.util.List;

public class OrganizationGroupListRestDTO {

    private List<OrganizationGroupRestDTO> groups;

    public OrganizationGroupListRestDTO() {
        this.groups = new ArrayList<OrganizationGroupRestDTO>();
    }

    public List<OrganizationGroupRestDTO> getGroups() {
        return groups;
    }

    public void setGroups(List<OrganizationGroupRestDTO> groups) {
        this.groups = groups;
    }

    public void addGroup(OrganizationGroupRestDTO group) {
        groups.add(group);
    }
}
