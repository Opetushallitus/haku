package fi.vm.sade.oppija.ui.service.impl;

import fi.vm.sade.generic.service.AbstractPermissionService;
import fi.vm.sade.oppija.common.authentication.AuthenticationService;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.ui.HakuPermissionService;
import fi.vm.sade.security.OrganisationHierarchyAuthorizer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Profile("default")
public class HakuPermissionServiceImpl extends AbstractPermissionService implements HakuPermissionService {

    private AuthenticationService authenticationService;
    private static final Logger log = LoggerFactory.getLogger(HakuPermissionServiceImpl.class);

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
        return userCanAccessApplication(application, getReadUpdateRole(), getCreateReadUpdateDeleteRole());
    }

    @Override
    public boolean userCanDeleteApplication(Application application) {
        return userCanAccessApplication(application, getCreateReadUpdateDeleteRole());
    }

    private boolean userCanAccessApplication(Application application, String... roles) {
        Map<String, String> answers = application.getVastauksetMerged();
        for (int i = 1; i <= 5; i++) {
            String id = "preference" + i + "-Opetuspiste-id";
            String parents = "preference" + i + "-Opetuspiste-id-parents";
            String organization = answers.get(id);
            if (StringUtils.isNotEmpty(organization) &&
                    checkAccess(organization, roles)) {
                log.debug("User can read application, org: {}", organization);
                return true;
            }
            for (String parent : parents.split(",")) {
                organization = answers.get(parent);
                if (StringUtils.isNotEmpty(organization) &&
                        checkAccess(organization, roles)) {
                    log.debug("User can read application, parent org: {}", organization);
                    return true;
                }
            }
        }
        return false;
    }

}
