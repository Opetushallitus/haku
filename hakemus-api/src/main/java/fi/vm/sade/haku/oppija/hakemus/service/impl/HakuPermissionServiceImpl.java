package fi.vm.sade.haku.oppija.hakemus.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.generic.service.AbstractPermissionService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.AuthorizationMeta;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.security.OrganisationHierarchyAuthorizer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.ws.rs.HEAD;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Profile(value = {"default"})
public class HakuPermissionServiceImpl extends AbstractPermissionService implements HakuPermissionService {

    private AuthenticationService authenticationService;
    private static final Logger log = LoggerFactory.getLogger(HakuPermissionServiceImpl.class);
    private static final String ROLE_OPO = "APP_HAKEMUS_OPO";
    private static final String ROLE_LISATIETORU = "APP_HAKEMUS_LISATIETORU";
    private static final String ROLE_LISATIETOCRUD = "APP_HAKEMUS_LISATIETOCRUD";

    @Autowired
    public HakuPermissionServiceImpl(AuthenticationService authenticationService,
                                     OrganisationHierarchyAuthorizer authorizer) {
        super("HAKEMUS");
        this.authenticationService = authenticationService;
        this.setAuthorizer(authorizer);
    }

    @Override
    public List<String> userCanReadApplications() {
        return userCanReadApplications(authenticationService.getOrganisaatioHenkilo());
    }

    @Override
    public List<String> userCanReadApplications(List<String> organizations) {
        List<String> readble = new ArrayList<String>();
        for (String organization : organizations) {
            log.debug("Checking read permissions as organization:{} for user:{})", organization, authenticationService.getCurrentHenkilo().getPersonOid());
            if (checkAccess(organization, getReadRole(),getReadUpdateRole(),getCreateReadUpdateDeleteRole(),getRoleLisatietoRU(),getRoleLisatietoCRUD())) {
                log.debug("Can read");
                readble.add(organization);
            }
        }
        return readble;
    }

    @Override
    public List<String> userHasOpoRole() {
        return userHasOpoRole(authenticationService.getOrganisaatioHenkilo());
    }

    @Override
    public List<String> userHasOpoRole(List<String> organizations) {
        List<String> opoOrg = new ArrayList<String>();
        for (String organization : organizations) {
            log.debug("checking opo-role against organization "+organization);
            if (checkAccess(organization, getOpoRole())) {
                opoOrg.add(organization);
            }
        }
        return opoOrg;
    }

    @Override
    public boolean userCanReadApplication(Application application) {
        log.debug("Checking access for application: "+application.getOid());
        boolean canRead = userCanAccessApplication(application, getReadRole(), getReadUpdateRole(), getCreateReadUpdateDeleteRole(),
                getRoleLisatietoRU(), getRoleLisatietoCRUD());
        if (canRead) {
            log.debug("Can read, "+application.getOid());
            return canRead;
        }
        boolean opo = userHasOpoRoleToSendingSchool(application);
        AuthorizationMeta authorizationMeta = application.getAuthorizationMeta();
        boolean opoAllowed = authorizationMeta == null || authorizationMeta.isOpoAllowed() == null
                ? false : authorizationMeta.isOpoAllowed();
        if (opo && opoAllowed) {
            return true;
        }
        if ((authorizationMeta == null || authorizationMeta.getAllAoOrganizations().isEmpty())
                && userCanEnterApplication()) {
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Boolean> userHasEditRoleToPhases(Application application, Form form) {
        String userOid = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userOid == null || userOid.equals(application.getPersonOid())) {
            return Maps.newHashMap();
        }
        Map<String, Boolean> phasesToEdit = Maps.newHashMap();
        List<String> userRolesToApplication = Lists.newArrayList();
        if (userCanAccessApplication(application, getReadUpdateRole())) {
            userRolesToApplication.add(getReadUpdateRole());
        }
        if (userCanAccessApplication(application, getCreateReadUpdateDeleteRole())) {
            userRolesToApplication.add(getCreateReadUpdateDeleteRole());
        }
        if (userHasOpoRoleToSendingSchool(application)) {
            userRolesToApplication.add(getOpoRole());
        }
        for (Element element : form.getChildren()) {
            Phase phase = (Phase) element;
            String phaseId = phase.getId();
            boolean phaseLocked = Boolean.valueOf(application.getMetaValue(phaseId + "_locked"));
            Boolean editAllowed = !phaseLocked && phase.isEditAllowedByRoles(userRolesToApplication);
            phasesToEdit.put(phaseId, editAllowed);
        }
        return phasesToEdit;
    }

    @Override
    public boolean userCanDeleteApplication(Application application) {
        return userCanAccessApplication(application, getCreateReadUpdateDeleteRole());
    }

    @Override
    public boolean userCanPostProcess(Application application) {
        return checkAccess(getRootOrgOid(), getReadUpdateRole(), getCreateReadUpdateDeleteRole());
    }

    public final String getOpoRole() {
        return ROLE_OPO;
    }

    public static String getRoleLisatietoRU() {
        return ROLE_LISATIETORU;
    }

    public static String getRoleLisatietoCRUD() {
        return ROLE_LISATIETOCRUD;
    }

    private boolean userHasOpoRoleToSendingSchool(Application application) {
        AuthorizationMeta authorizationMeta = application.getAuthorizationMeta();
        if (authorizationMeta == null) {
            return false;
        }

        Set<String> sendingSchool = authorizationMeta.getSendingSchool();
        if (sendingSchool == null) {
            return false;
        }
        for (String organization : sendingSchool) {
            if (!Strings.isNullOrEmpty(organization) && checkAccess(organization, getOpoRole())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean userCanEnterApplication() {
        for (String org : authenticationService.getOrganisaatioHenkilo()) {
            if (checkAccess(org, getCreateReadUpdateDeleteRole())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean userCanSearchBySendingSchool() {
        if (checkAccess(getRootOrgOid(), getReadRole(), getReadUpdateRole(), getCreateReadUpdateDeleteRole(),
                getOpoRole())) {
            return true;
        }
        return userHasOpoRole().size() > 0;
    }

    @Override
    public boolean userCanEditApplicationAdditionalData(Application application) {
        return userCanAccessApplication(application, getRoleLisatietoCRUD(), getRoleLisatietoRU(), getReadUpdateRole(), getCreateReadUpdateDeleteRole());
    }

    @SuppressWarnings("deprecation")
    private boolean userCanAccessApplication(Application application, String... roles) {
        if (checkAccess(getRootOrgOid(), roles)) {
            // OPH users can access anything
            return true;
        }

        AuthorizationMeta authorizationMeta = application.getAuthorizationMeta();
        if (authorizationMeta == null) {
            return userCanEnterApplication();
        }

        Set<String> allOrganizations = authorizationMeta.getAllAoOrganizations();
        if (allOrganizations == null) {
            return userCanEnterApplication();
        }

        for (String organization : allOrganizations) {
            if (StringUtils.isNotEmpty(organization) &&
                    checkAccess(organization, roles)) {
                log.debug("User can read application, org: {}", organization);
                return true;
            }
        }

        return false;
    }

}
