package fi.vm.sade.haku.oppija.postprocess.impl;

import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultItemDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParametersBuilder;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.postprocess.EligibilityCheckWorker;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService.YO_TUTKINTO_KOMO;

@Service
public class EligibilityCheckWorkerImpl implements EligibilityCheckWorker {

    private final SuoritusrekisteriService suoritusrekisteriService;
    private final HakuService hakuService;
    private final ApplicationService applicationService;
    private static final int PERSON_BATCH = 1000;

    @Autowired
    public EligibilityCheckWorkerImpl(final SuoritusrekisteriService suoritusrekisteriService,
                                      final HakuService hakuService, final ApplicationService applicationService) {
        this.suoritusrekisteriService = suoritusrekisteriService;
        this.hakuService = hakuService;
        this.applicationService = applicationService;
    }

    @Override
    public void checkEligibilities() {
        List<String> personOids = suoritusrekisteriService.getChanges(YO_TUTKINTO_KOMO, yesterday());
        List<ApplicationSystem> ass = hakuService.getApplicationSystems(true);
        // TODO ohjausparameista haun päättymispäivä, käsittele vain relevantit haut
        for (ApplicationSystem as : ass) {
            List<String> aos = as.getAosForAutomaticEligibility();
            if (aos == null || aos.isEmpty()) {
                continue;
            }
            int idx = 0;
            List<String> personBatch = personOids.subList(idx, Math.min(idx + PERSON_BATCH, personOids.size()));
            while (!personBatch.isEmpty()) {
                ApplicationQueryParameters params = new ApplicationQueryParametersBuilder()
                        .setAsId(as.getId())
                        .setAoOids(aos)
                        .setPersonOids(personBatch)
                        .build();
                ApplicationSearchResultDTO result = applicationService.findApplications(params);
                List<String> toRedo = new ArrayList<>(result.getTotalCount());
                for (ApplicationSearchResultItemDTO resultItem : result.getResults()) {
                    toRedo.add(resultItem.getOid());
                }
                if (!toRedo.isEmpty()) {
                    applicationService.massRedoPostProcess(toRedo, Application.PostProcessingState.NOMAIL);
                }
                idx = Math.min(idx + PERSON_BATCH, personOids.size());
                personBatch = personOids.subList(idx, Math.min(idx + PERSON_BATCH, personOids.size()));
            }
        }
    }

    private Date yesterday() {
        long oneDay = 1000 * 60 * 60 * 24;
        return new Date(System.currentTimeMillis() - oneDay);
    }
}
