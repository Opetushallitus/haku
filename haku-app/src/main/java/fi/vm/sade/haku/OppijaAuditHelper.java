package fi.vm.sade.haku;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;

public class OppijaAuditHelper {
    public static final Audit AUDIT = new Audit("haku-app", ApplicationType.OPISKELIJA);
}
