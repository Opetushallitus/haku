package fi.vm.sade.haku;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OPPIJA_AUDIT logger appender
 */
public class OppijaAuditHelper implements fi.vm.sade.auditlog.Logger {
    private final static Logger LOGGER = LoggerFactory.getLogger(OppijaAuditHelper.class);
    @Override
    public void log(String msg) {
        LOGGER.info(msg);
    }
}
