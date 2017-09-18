package fi.vm.sade.haku;

import fi.vm.sade.auditlog.*;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class VirkailijaAuditLogger extends Audit {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(VirkailijaAuditLogger.class);

    public VirkailijaAuditLogger() {
        super(new AuditHelper(), "haku-app", ApplicationType.VIRKAILIJA);
    }

    public Oid getCurrentPersonOid() {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context == null) {
            return null;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        String personOid = auth.getName();
        try {
            return new Oid(personOid);
        } catch (GSSException e) {
            LOGGER.error("Error creating Oid-object out of {}", personOid);
            return null;
        }
    }
}
