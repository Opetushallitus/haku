package fi.vm.sade.haku;

import fi.vm.sade.auditlog.ApplicationType;
import org.springframework.stereotype.Component;

@Component
public class OppijaAuditLogger extends HakuAuditLogger {
    public OppijaAuditLogger() {
        super(new OppijaAuditHelper(), "haku-app", ApplicationType.OPPIJA);
    }
}
