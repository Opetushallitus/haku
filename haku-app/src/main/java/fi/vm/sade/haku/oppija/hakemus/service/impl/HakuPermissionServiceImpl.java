package fi.vm.sade.haku.oppija.hakemus.service.impl;

import fi.vm.sade.generic.service.AbstractPermissionService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.security.OrganisationHierarchyAuthorizer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Profile("default")
public class HakuPermissionServiceImpl extends AbstractPermissionService implements HakuPermissionService {

    public static final int MAX_NUMBER_OF_PREFERENCES = 5;
    private AuthenticationService authenticationService;
    private static final Logger LOG = LoggerFactory.getLogger(HakuPermissionServiceImpl.class);

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
            LOG.debug("Calling checkAccess({}, {})", organization, getReadRole());
            if (checkAccess(organization, getReadRole())) {
                LOG.debug("Can read");
                readble.add(organization);
            } else if (checkAccess(organization, getReadUpdateRole())) {
                readble.add(organization);
            } else if (checkAccess(organization, getCreateReadUpdateDeleteRole())) {
                readble.add(organization);
            }
        }
        return readble;
    }

    @Override
    public boolean userCanReadApplication(Application application) {
        return userCanAccessApplication(application, getReadRole(), getReadUpdateRole(), getCreateReadUpdateDeleteRole());
    }

    @Override
    public boolean userCanUpdateApplication(Application application) {
        String userOid = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userOid == null || userOid.equals(application.getPersonOid())) {
            return false;
        }
        return userCanAccessApplication(application, getReadUpdateRole(), getCreateReadUpdateDeleteRole());
    }

    @Override
    public boolean userCanDeleteApplication(Application application) {
        return userCanAccessApplication(application, getCreateReadUpdateDeleteRole());
    }

    @Override
    public boolean userCanPostProcess(Application application) {
        return checkAccess(getRootOrgOid(), getReadUpdateRole(), getCreateReadUpdateDeleteRole());
    }

    @Override
    public boolean userCanEnterApplication() {
        return checkAccess(getRootOrgOid(), getCreateReadUpdateDeleteRole());
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

            if (i == 1 && StringUtils.isEmpty(organization)) {
                return true; // Anyone can read empty application
            }

            if (StringUtils.isNotEmpty(organization) &&
                    checkAccess(organization, roles)) {
                LOG.debug("User can read application, org: {}", organization);
                return true;
            }
        }
        return false;
    }

}
