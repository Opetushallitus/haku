package fi.vm.sade.oppija.ui.service.impl;

import fi.vm.sade.generic.service.AbstractPermissionService;
import fi.vm.sade.oppija.common.authentication.AuthenticationService;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.ui.HakuPermissionService;
import fi.vm.sade.security.OrganisationHierarchyAuthorizer;
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
            } else  if (checkAccess(organization, getCreateReadUpdateDeleteRole())) {
                readble.add(organization);
            }
        }
        return readble;
    }

    @Override
    public boolean userCanReadApplication(Application application) {
        Map<String, String> answers = application.getVastauksetMerged();
        for (int i = 1; i <= 5; i++) {
            String id = "preference"+i+"-Opetuspiste-id";
            String parents = "preference"+i+"-Opetuspiste-id-parents";
            if (checkAccess(answers.get(id), getReadRole(), getReadUpdateRole(), getCreateReadUpdateDeleteRole())) {
                return true;
            }
            for (String parent : parents.split(",")) {
                if (checkAccess(answers.get(parent), getReadRole(), getReadUpdateRole(), getCreateReadUpdateDeleteRole())) {
                    return true;
                }
            }
        }
        return false;
    }

}
