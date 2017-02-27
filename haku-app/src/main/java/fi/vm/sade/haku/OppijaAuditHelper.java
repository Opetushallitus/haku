package fi.vm.sade.haku;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OppijaAuditHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(OppijaAuditHelper.class);
    public static final Audit AUDIT = new Audit(LOGGER, "haku-app", ApplicationType.OPISKELIJA);
}
