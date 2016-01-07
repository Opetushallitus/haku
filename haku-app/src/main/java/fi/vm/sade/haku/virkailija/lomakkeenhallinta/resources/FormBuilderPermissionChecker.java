package fi.vm.sade.haku.virkailija.lomakkeenhallinta.resources;

import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.organization.resource.OrganizationResource;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service("formBuilderPermissionChecker")
public class FormBuilderPermissionChecker {

    public static final String ROLE_GENERATE_HAKU = "ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD";
    public static final String ROLE_GENERATE_ALL_HAKUS = ROLE_GENERATE_HAKU + "_" + OrganizationResource.ORGANIZATION_ROOT_ID;

    @Autowired
    private HakuService hakuService;

    @Autowired
    private OrganizationService organizationService;

    public boolean isAllowedToGenerateHaku(String oid) throws UnsupportedEncodingException {
        HakuV1RDTO hakuV1RDTO = hakuService.getRawApplicationSystem(oid);
        Set<String> formRoles = getFormAdminRoles();

        for (String tarjoajaOid : hakuV1RDTO.getTarjoajaOids()) {
            if (formRoles.contains(ROLE_GENERATE_HAKU + "_" + tarjoajaOid)) {
                return true;
            }
        }

        return false;
    }

    private Set<String> getCasRoles() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Set<String> casRoles = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            casRoles.add(authority.getAuthority());
        }
        return casRoles;
    }

    private Set<String> getFormAdminRoles() throws UnsupportedEncodingException {
        Set<String> formAdminRoles = new HashSet<>();

        // Add roles for child organizations as well
        for (String role : getCasRoles()) {
            if (role.startsWith(ROLE_GENERATE_HAKU + "_")) {
                String[] parts = role.split("_");
                final String orgOid = parts[parts.length - 1];

                OrganisaatioSearchCriteria criteria = new OrganisaatioSearchCriteria(){{
                    this.getOidRestrictionList().add(orgOid);
                }};

                for (Organization organization : organizationService.search(criteria)) {
                    formAdminRoles.add(ROLE_GENERATE_HAKU + "_" + organization.getOid());
                }
            }
        }

        return formAdminRoles;
    }

}
