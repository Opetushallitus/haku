package fi.vm.sade.haku.oppija.hakemus.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fi.vm.sade.generic.service.AbstractPermissionService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile(value = {"default"})
public class HakuPermissionServiceImpl extends AbstractPermissionService implements HakuPermissionService {

    public static final int MAX_NUMBER_OF_PREFERENCES = 5;
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
    public List<String> userCanReadApplications(List<String> organizations) {

        if (organizations == null || organizations.isEmpty()) {
            organizations = authenticationService.getOrganisaatioHenkilo();
        }

        List<String> readble = new ArrayList<String>();
        for (String organization : organizations) {
            log.debug("Calling checkAccess({}, {})", organization, getReadRole());
            if (checkAccess(organization, getReadRole())) {
                log.debug("Can read");
                readble.add(organization);
            } else if (checkAccess(organization, getReadUpdateRole())) {
                readble.add(organization);
            } else if (checkAccess(organization, getCreateReadUpdateDeleteRole())) {
                readble.add(organization);
            } else if (checkAccess(organization, getRoleLisatietoRU())) {
                readble.add(organization);
            } else if (checkAccess(organization, getRoleLisatietoCRUD())) {
                readble.add(organization);
            }
        }
        return readble;
    }

    @Override
    public List<String> userHasOpoRole(List<String> organizations) {
        if (organizations == null || organizations.isEmpty()) {
            organizations = authenticationService.getOrganisaatioHenkilo();
        }
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
        if (opo) {
            log.debug("Can read, opo "+application.getOid());
        }
        return opo;
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
            phasesToEdit.put(phase.getId(), phase.isEditAllowedByRoles(userRolesToApplication));
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
        Map<String, String> answers = new HashMap<String, String>(application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION));
        if (answers.containsKey(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL)) {
            String organization = answers.get(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL);
            if (!Strings.isNullOrEmpty(organization)) {
                return checkAccess(organization, getOpoRole());
            }
        }
        return false;
    }

    @Override
    public boolean userCanEnterApplication() {
        return checkAccess(getRootOrgOid(), getCreateReadUpdateDeleteRole());
    }

    @Override
    public boolean userCanSearchBySendingSchool() {
        if (checkAccess(getRootOrgOid(), getReadRole(), getReadUpdateRole(), getCreateReadUpdateDeleteRole(),
                getOpoRole())) {
            // OPH users can access anything
            return true;
        }
        return userHasOpoRole(null).size() > 0;
    }

    @Override
    public boolean userCanEditApplicationAdditionalData(Application application) {
        if (userCanAccessApplication(application, getRoleLisatietoCRUD())) {
            return true;
        } else if (userCanAccessApplication(application, getRoleLisatietoRU())) {
           return true;
        } else if (userCanAccessApplication(application, getReadUpdateRole())) {
            return true;
        } else if (userCanAccessApplication(application, getCreateReadUpdateDeleteRole())) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private boolean userCanAccessApplication(Application application, String... roles) {
        if (checkAccess(getRootOrgOid(), roles)) {
            // OPH users can access anything
            return true;
        }

        Map<String, String> answers = application.getVastauksetMerged();
        for (int i = 1; i <= MAX_NUMBER_OF_PREFERENCES; i++) {
            String id = "preference" + i + "-Opetuspiste-id";
            String organization = answers.get(id);

            if (StringUtils.isNotEmpty(organization) &&
                    checkAccess(organization, roles)) {
                log.debug("User can read application, org: {}", organization);
                return true;
            }
        }
        return false;
    }

}
