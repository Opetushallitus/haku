package fi.vm.sade.haku.oppija.postprocess.impl;

import fi.vm.sade.haku.healthcheck.StatusRepository;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultItemDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.*;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.postprocess.EligibilityCheckWorker;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.OhjausparametritService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.domain.Ohjausparametrit;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService.YO_TUTKINTO_KOMO;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.ROOT_ORGANIZATION_OID;

@Service
public class EligibilityCheckWorkerImpl implements EligibilityCheckWorker {

    private static final Logger log = LoggerFactory.getLogger(EligibilityCheckWorkerImpl.class);

    private final SuoritusrekisteriService suoritusrekisteriService;
    private final HakuService hakuService;
    private final OhjausparametritService ohjausparametritService;
    private final ApplicationDAO applicationDAO;
    private final StatusRepository statusRepository;
    private static final int PERSON_BATCH = 1000;

    @Autowired
    public EligibilityCheckWorkerImpl(final SuoritusrekisteriService suoritusrekisteriService,
                                      final HakuService hakuService,
                                      final ApplicationDAO applicationDAO, final StatusRepository statusRepository,
                                      final OhjausparametritService ohjausparametritService
    ) {
        this.suoritusrekisteriService = suoritusrekisteriService;
        this.hakuService = hakuService;
        this.applicationDAO = applicationDAO;
        this.statusRepository = statusRepository;
        this.ohjausparametritService = ohjausparametritService;
    }

    @Override
    public void checkEligibilities(Date since) {
        since = since != null
                ? since
                : new Date(1L);
        List<String> personOids = suoritusrekisteriService.getChanges(YO_TUTKINTO_KOMO, since);
        List<ApplicationSystem> ass = hakuService.getApplicationSystems(false);
        for (ApplicationSystem asWithOutHakuKohteet : ass) {
            if(hasValidOhjausparametriWithAutomaticHakukelpoisuus(asWithOutHakuKohteet)) {
                checkEligibilities(asWithOutHakuKohteet.getId(), personOids);
            }
        }
    }

    private void checkEligibilities(String asOid, List<String> personOids) {
        log.debug("Processing applicationSystem {}", asOid);
        ApplicationSystem as = hakuService.getApplicationSystem(asOid);
        if (hasHakukohteitaWithAutomaticHakukelpoisuus(as)) {
            List<String> aos = as.getAosForAutomaticEligibility();
            statusRepository.startOperation(SCHEDULER_ELIGIBILITY_CHECK, as.getId());
            int idx = 0;
            List<String> personBatch = personOids.subList(idx, Math.min(idx + PERSON_BATCH, personOids.size()));
            while (!personBatch.isEmpty()) {
                log.debug("Processing changes for applicationSystem {}, batch idx {}", as.getId(), idx);
                ApplicationQueryParameters queryParams = new ApplicationQueryParametersBuilder()
                        .setAsId(as.getId())
                        .setAoOids(aos)
                        .setPersonOids(personBatch)
                        .build();
                ApplicationFilterParameters filterParams = new ApplicationFilterParametersBuilder()
                        .addOrganizationsReadable(Collections.singletonList(ROOT_ORGANIZATION_OID))
                        .setHakutapa(as.getHakutapa())
                        .setKohdejoukko(as.getKohdejoukkoUri())
                        .setMaxApplicationOptions(as.getMaxApplicationOptions())
                        .build();
                ApplicationSearchResultDTO result = applicationDAO.findAllQueried(queryParams, filterParams);
                List<String> toRedo = new ArrayList<>(result.getTotalCount());
                for (ApplicationSearchResultItemDTO resultItem : result.getResults()) {
                    toRedo.add(resultItem.getOid());
                }
                log.debug("Processing changes for applicationSystem {}, batch idx {}, to redo {}", as.getId(), idx, toRedo.size());
                if (!toRedo.isEmpty()) {
                    applicationDAO.massRedoPostProcess(toRedo, Application.PostProcessingState.NOMAIL);
                }
                idx = Math.min(idx + PERSON_BATCH, personOids.size());
                personBatch = personOids.subList(idx, Math.min(idx + PERSON_BATCH, personOids.size()));
            }
            statusRepository.endOperation(SCHEDULER_ELIGIBILITY_CHECK, as.getId());
        }
        log.debug("Done processing applicationSystem {}", asOid);
    }

    private boolean hasHakukohteitaWithAutomaticHakukelpoisuus(ApplicationSystem as) {
        List<String> aos = as.getAosForAutomaticEligibility();
        return !(aos == null || aos.isEmpty());
    }

    private boolean hasValidOhjausparametriWithAutomaticHakukelpoisuus(ApplicationSystem as) {
        Ohjausparametrit ohjausparametrit;
        try {
            ohjausparametrit = ohjausparametritService.fetchOhjausparametritForHaku(as.getId());
            ohjausparametrit.getPH_AHP(); // Catch NPE here!
        } catch(Throwable t) {
            log.error("Unable to fetch 'ohjausparametrit' to 'haku' {}! Skipping processing!", as.getId(), t);
            return false;
        }
        if(ohjausparametrit.getPH_AHP() != null && ohjausparametrit.getPH_AHP().getDate() != null) {
            final Date NOW = new Date();
            final Date automaattinenHakukelpoisuusPaattyy = ohjausparametrit.getPH_AHP().getDate();
            if(NOW.after(automaattinenHakukelpoisuusPaattyy)) {
                log.warn("Now is after 'automaattinenHakukelpoisuusPaattyy' so skipping 'haku' {}", as.getId());
                return false;
            }
        }
        return true;
    }
}
