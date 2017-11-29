package fi.vm.sade.haku;

import fi.vm.sade.auditlog.ApplicationType;
import org.springframework.stereotype.Component;

@Component
public class VirkailijaAuditLogger extends HakuAuditLogger {
    public VirkailijaAuditLogger() {
        super(new AuditHelper(), "haku-app", ApplicationType.VIRKAILIJA);
    }

}
