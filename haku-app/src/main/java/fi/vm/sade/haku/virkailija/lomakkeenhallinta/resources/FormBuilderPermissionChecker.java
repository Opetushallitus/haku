package fi.vm.sade.haku.virkailija.lomakkeenhallinta.resources;

import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.organization.resource.OrganizationResource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service("formBuilderPermissionChecker")
public class FormBuilderPermissionChecker {

    public static final String ROLE_GENERATE_HAKU = "ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD";
    public static final String ROLE_GENERATE_ALL_HAKUS = ROLE_GENERATE_HAKU + "_" + OrganizationResource.ORGANIZATION_ROOT_ID;

    @Autowired
    private HakuService hakuService;

    public boolean isAllowedToGenerateHaku(String oid) {
        Set<String> casRoles = getCasRoles();

        HakuV1RDTO hakuV1RDTO = hakuService.getRawApplicationSystem(oid);

        for (String tarjoajaOid : hakuV1RDTO.getTarjoajaOids()) {
            if (casRoles.contains(ROLE_GENERATE_HAKU + "_" + tarjoajaOid)) {
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

}
