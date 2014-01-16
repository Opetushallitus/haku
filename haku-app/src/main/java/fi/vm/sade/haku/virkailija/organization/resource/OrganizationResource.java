/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.haku.virkailija.organization.resource;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@Path("/organization")
@PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD')")
public class OrganizationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationResource.class);

    public static final String ORGANIZATION_ROOT_ID = "1.2.246.562.10.00000000001";

    @Autowired
    private OrganizationService organizationService;

    @GET
    @Path("/hakemus")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<Map<String, Object>> searchJson(@QueryParam("searchString") final String searchString,
                                                @QueryParam("organizationType") final String organizationType,
                                                @QueryParam("learningInstitutionType") final String learningInstitutionType,
                                                @QueryParam("includePassive") @DefaultValue("false") final boolean includePassive,
                                                @QueryParam("includePlanned") @DefaultValue("false") final boolean includePlanned) {
        LOGGER.debug("Search organizations q: {}, orgType: {}, loiType: {}, passive: {}, planned: {} ",
                new String[] {searchString, organizationType, learningInstitutionType, String.valueOf(includePassive),
                        String.valueOf(includePlanned)});
        List<Organization> listOfOrganization = getOrganizations(searchString, organizationType,
                learningInstitutionType, includePassive, includePlanned);
        return toMap(listOfOrganization);
    }

    private List<Map<String, Object>> toMap(final List<Organization> listOfOrganization) {
        Collection<Organization> roots = Collections2.filter(listOfOrganization, new Predicate<Organization>() {
            @Override
            public boolean apply(final Organization organization) {
                return ORGANIZATION_ROOT_ID.equals(organization.getParentOid());
            }
        });
        LOGGER.debug("toMap, root organizations: {}", roots.size());
        List<Map<String, Object>> result = Lists.newArrayList();
        for (Organization organization : roots) {
            Map<String, Object> org = Maps.newHashMap();
            org.put("organization", organization);
            org.put("children", getChildren(listOfOrganization, organization.getOid()));
            result.add(org);
        }
        LOGGER.debug("toMap, {} organizations", result.size());
        return result;
    }

    private List<Map<String, Object>> getChildren(final List<Organization> listOfOrganization, final String parentOid) {

        Collection<Organization> children = Collections2.filter(listOfOrganization, new Predicate<Organization>() {
            @Override
            public boolean apply(final Organization organization) {
                return parentOid.equals(organization.getParentOid());
            }
        });
        List<Map<String, Object>> result = Lists.newArrayList();
        for (Organization organization : children) {
            Map<String, Object> org = Maps.newHashMap();
            org.put("organization", organization);
            org.put("children", getChildren(listOfOrganization, organization.getOid()));
            result.add(org);
        }
        return result;
    }

    private List<Organization> getOrganizations(final String searchString,
                                                final String organizationType,
                                                final String learningInstitutionType,
                                                final boolean includePassive,
                                                final boolean includePlanned) {
        LOGGER.debug("getOrganizations {} {} {} {}",
                new String[]{searchString, organizationType, learningInstitutionType,
                        String.valueOf(includePassive), String.valueOf(includePlanned)});
        OrganisaatioSearchCriteria criteria = new OrganisaatioSearchCriteria();
        criteria.setSearchStr(searchString);
        criteria.setOrganisaatioTyyppi(organizationType);
        criteria.setOppilaitosTyyppi(learningInstitutionType);
        criteria.setLakkautetut(includePassive);
        criteria.setSuunnitellut(includePlanned);
        List<Organization> organizations = organizationService.search(criteria);
        LOGGER.debug("getOrganizations found {} organizations", organizations.size());
        if (LOGGER.isDebugEnabled()) {
            for (Organization org : organizations) {
                LOGGER.debug(org.toString());
            }
        }
        return organizations;
    }
}
