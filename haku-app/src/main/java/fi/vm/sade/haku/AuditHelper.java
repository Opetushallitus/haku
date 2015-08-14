package fi.vm.sade.haku;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.LogMessage;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

/**
 * @author Jussi Jartamo
 */
public class AuditHelper {
    public static final Audit AUDIT = new Audit("haku-app", ApplicationType.VIRKAILIJA);

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
