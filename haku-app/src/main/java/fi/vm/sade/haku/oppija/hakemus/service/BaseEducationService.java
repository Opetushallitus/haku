package fi.vm.sade.haku.oppija.hakemus.service;

import fi.vm.sade.haku.oppija.common.suoritusrekisteri.ArvosanaDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;

import java.util.Map;

public interface BaseEducationService {
    Application addBaseEducation(Application application);
    Application addSendingSchool(Application application);

    Map<String, ArvosanaDTO> getArvosanat(String personOid, String baseEducation, ApplicationSystem applicationSystem);
}
