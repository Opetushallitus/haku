package fi.vm.sade.oppija.haku.aspect;


import fi.vm.sade.log.client.Logger;
import fi.vm.sade.log.client.LoggerMock;
import fi.vm.sade.log.model.Tapahtuma;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private Logger logger;

    /**
     * Logs event when a form phase is successfully saved
     * as application data in data store.
     */
    @AfterReturning("execution(* fi.vm.sade.oppija.haku.dao.ApplicationDAO.tallennaVaihe(..)) && args(hakemusState,..)")
    public void logSavePhase(HakemusState hakemusState) {
        try {
            Tapahtuma t = new Tapahtuma();
            t.setMuutoksenKohde("Application, form id: " + hakemusState.getHakemus().getHakuLomakeId().getApplicationPeriodId()
                    + ", user: " + hakemusState.getHakemus().getUser().getUserName());
            t.setAikaleima(new Date());
            t.setKenenPuolesta("" + hakemusState.getHakemus().getUser().getUserName());
            t.setKenenTietoja("" + hakemusState.getHakemus().getUser().getUserName());
            t.setTapahtumatyyppi("save application phase");
            t.setTekija("" + hakemusState.getHakemus().getUser().getUserName());
            t.setUusiArvo("new");
            t.setVanhaArvo("old");
            logger.log(t);
        } catch (Exception e) {
            LOGGER.warn("Could not log tallennaVaihe event");
        }
    }

}
