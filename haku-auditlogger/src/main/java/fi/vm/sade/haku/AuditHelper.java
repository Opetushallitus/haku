package fi.vm.sade.haku;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AUDIT_VIRKAILIJA logger appender
 */
public class AuditHelper implements fi.vm.sade.auditlog.Logger {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuditHelper.class);

    @Override
    public void log(String msg) {
        LOGGER.info(msg);
    }
}
