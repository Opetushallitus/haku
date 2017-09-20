package fi.vm.sade.haku;

import fi.vm.sade.auditlog.ApplicationType;
import org.springframework.stereotype.Component;

@Component
public class ApiAuditLogger extends HakuAuditLogger {

    public ApiAuditLogger() {
        super(new ApiAuditHelper(), "haku-app", ApplicationType.BACKEND);
    }
}
