package fi.vm.sade.haku;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.haku.LogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

public class ApiAuditHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(ApiAuditHelper.class);
    public static final Audit AUDIT = new Audit(LOGGER, "hakemus-api", ApplicationType.BACKEND);

    public static LogMessage.LogMessageBuilder builder() {
        return LogMessage.builder().id(getUsernameFromSession());
    }
    private static String getUsernameFromSession() {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context != null) {
            Principal p = (Principal) context.getAuthentication();
            if(p != null) {
                return p.getName();
            }
        }
        return "Anonymous user";
    }
}

