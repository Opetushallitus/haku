package fi.vm.sade.haku.oppija.hakemus.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.authorization.NotAuthorizedException;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.AuthorizationMeta;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.KayttooikeusService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.javautils.opintopolku_spring_security.OrganisationHierarchyAuthorizer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fi.vm.sade.haku.oppija.hakemus.service.Role.*;

@Service
@Profile(value = {"default", "vagrant"})
public class HakuPermissionServiceImpl implements HakuPermissionService {

    private AuthenticationService authenticationService;
    private KayttooikeusService kayttoikeusService;
    private ApplicationSystemService applicationSystemService;
    private OrganisationHierarchyAuthorizer authorizer;
    private static final Logger log = LoggerFactory.getLogger(HakuPermissionServiceImpl.class);

    private final String rootOrganizationOid;

    @Autowired
    public HakuPermissionServiceImpl(AuthenticationService authenticationService,
                                     KayttooikeusService kayttoikeusService,
                                     ApplicationSystemService applicationSystemService,
                                     OrganisationHierarchyAuthorizer authorizer,
                                     @Value("${root.organisaatio.oid}") final String rootOrganizationOid) {
        this.authenticationService = authenticationService;
        this.kayttoikeusService = kayttoikeusService;
        this.applicationSystemService = applicationSystemService;
        this.authorizer = authorizer;
        this.rootOrganizationOid = rootOrganizationOid;
    }

    @Override
    public List<String> userCanReadApplications() {
        return userCanReadApplications(kayttoikeusService.getOrganisaatioHenkilo());
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
        return userHasOpoRole(kayttoikeusService.getOrganisaatioHenkilo());
    }

    @Override
    public List<String> userHasHetuttomienKasittelyRole() {
        return userHasHetuttomienKasittelyRole(kayttoikeusService.getOrganisaatioHenkilo());
    }

    @Override
    public List<String> userHasHetuttomienKasittelyRole(List<String> organizations) {
        return userHasRole(organizations, getRoleHetuttomienKasittely());
    }

    @Override
    public List<String> userHasOpoRole(List<String> organizations) {
        return userHasRole(organizations, getOpoRole());
    }

    private List<String> userHasRole(List<String> organizations, String role) {
        List<String> filteredOrgs = new ArrayList<String>();
        for (String organization : organizations) {
            log.debug("checking role: {} against organization: {}", role, organization);
            if (checkAccess(organization, role)) {
                filteredOrgs.add(organization);
            }
        }
        return filteredOrgs;
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

        Set<String> allAoOrganizations = authorizationMeta.getAllAoOrganizations();
        if ((authorizationMeta == null || allAoOrganizations.isEmpty())
                && userCanEnterApplication()) {
            return true;
        }

        ApplicationSystem as = applicationSystemService.getApplicationSystem(
                application.getApplicationSystemId(), "hakutapa", "hakukausiVuosi", "hakukausiUri", "kohdejoukkoUri");
        if (!userHasHetuttomienKasittelyRole().isEmpty()
                && OppijaConstants.HAKUTAPA_YHTEISHAKU.equals(as.getHakutapa())
                && OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(as.getKohdejoukkoUri())) {
            return true;
        }

        return false;
    }

    @Override
    public Map<String, Boolean> userHasEditRoleToPhases(ApplicationSystem as, Application application, Form form) {
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
        if (!userHasHetuttomienKasittelyRole().isEmpty()) {
            userRolesToApplication.add(getRoleHetuttomienKasittely());
        }
        for (Element element : form.getChildren()) {
            Phase phase = (Phase) element;
            String phaseId = phase.getId();
            boolean phaseLocked = Boolean.valueOf(application.getMetaValue(phaseId + "_locked"));
            Boolean editAllowed = !phaseLocked
                                  && phase.isEditAllowedByRoles(userRolesToApplication)
                                  && (!OppijaConstants.PHASE_GRADES.equals(phaseId) || userIsOPHUser() || isGradesEditingAllowed(as, application));
            phasesToEdit.put(phaseId, editAllowed);
        }
        return phasesToEdit;
    }

    protected static boolean isGradesEditingAllowed(ApplicationSystem as, Application application) {
        if(OppijaConstants.TOISEN_ASTEEN_HAKUJEN_KOHDEJOUKOT.contains(as.getKohdejoukkoUri())) {
            return application.isNew() || application.isDraft();
        }
        return true;
    }

    private boolean userIsOPHUser() {
        if(checkAccess(getRootOrgOid(), getReadUpdateRole(), getCreateReadUpdateDeleteRole())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean userCanDeleteApplication(Application application) {
        if (userCanAccessApplication(application, getCreateReadUpdateDeleteRole())) {
            return true;
        }

        ApplicationSystem as = applicationSystemService.getApplicationSystem(
                application.getApplicationSystemId(), "hakutapa", "hakukausiVuosi", "hakukausiUri", "kohdejoukkoUri");
        if (!userHasHetuttomienKasittelyRole().isEmpty()
                && OppijaConstants.HAKUTAPA_YHTEISHAKU.equals(as.getHakutapa())
                && OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(as.getKohdejoukkoUri())) {
            return true;
        }

        return false;
    }

    @Override
    public boolean userCanPostProcess(Application application) {

        if(checkAccess(getRootOrgOid(), getReadUpdateRole(), getCreateReadUpdateDeleteRole())) {
            return true;
        }

        ApplicationSystem as = applicationSystemService.getApplicationSystem(
                application.getApplicationSystemId(), "hakutapa", "hakukausiVuosi", "hakukausiUri", "kohdejoukkoUri");
        if(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(as.getKohdejoukkoUri())
                && userCanAccessApplication(application, getReadUpdateRole(), getCreateReadUpdateDeleteRole())) {
            return true;
        }

        return false;
    }

    private String getRootOrgOid() {
        return this.rootOrganizationOid;
    }

    public final String getRoleHetuttomienKasittely() {
        return ROLE_HETUTTOMIENKASITTELY.casName;
    }

    public final String getOpoRole() {
        return ROLE_OPO.casName;
    }

    public static String getReadRole() {
        return ROLE_R.casName;
    }

    public static String getReadUpdateRole() {
        return ROLE_RU.casName;
    }

    public static String getCreateReadUpdateDeleteRole() {
        return ROLE_CRUD.casName;
    }

    public static String getRoleLisatietoRU() {
        return ROLE_LISATIETORU.casName;
    }

    public static String getRoleLisatietoCRUD() {
        return ROLE_LISATIETOCRUD.casName;
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
        for (String org : kayttoikeusService.getOrganisaatioHenkilo()) {
            if (checkAccess(org, getCreateReadUpdateDeleteRole())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> userCanEnterApplications() {
        List<String> orgs = new ArrayList<String>();
        for (String org : kayttoikeusService.getOrganisaatioHenkilo()) {
            if (checkAccess(org, getCreateReadUpdateDeleteRole())) {
                orgs.add(org);
            }
        }
        return orgs;
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

    private boolean checkAccess(String targetOrganisaatioOid, String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            authorizer.checkAccess(authentication, targetOrganisaatioOid, roles);
            return true;
        } catch (NotAuthorizedException e) {
            log.debug("Not authorized for organization {}, exception: {}", targetOrganisaatioOid, e);
        }
        return false;
    }
}
