package fi.vm.sade.oppija.haku.aspect;


import fi.vm.sade.log.client.Logger;
import fi.vm.sade.log.client.LoggerMock;
import fi.vm.sade.log.model.Tapahtuma;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * An aspect that handel different logging operations.
 *
 * Logging is handled with a logger client module that passes the log events
 * to log-service.
 *
 * @author Hannu Lyytikainen
 */

@Aspect
@Component
public class LoggerAspect {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LoggerAspect.class);

    private final Logger logger;

    public LoggerAspect() {
        this.logger = new LoggerMock();
    }

    /**
     * Logs event when a form phase is successfully saved
     * as application data in data store.
     */
    @AfterReturning("execution(* fi.vm.sade.oppija.haku.dao.ApplicationDAO.tallennaVaihe(..))")
    public void logSavePhase() {
        try {
            Tapahtuma t = new Tapahtuma();
            t.setMuutoksenKohde("muutoksenkohde");
            t.setAikaleima(new Date());
            t.setKenenPuolesta("kenenpuolseta");
            t.setKenenTietoja("kenentietoja");
            t.setTapahtumatyyppi("tapahtumattyyppi");
            t.setTekija("tekija");
            t.setUusiArvo("uusi arvo");
            t.setVanhaArvo("vaha arvo");
            logger.log(t);
        } catch (Exception e) {
            LOGGER.warn("Could not log tallennaVaihe event");
        }
    }

}
