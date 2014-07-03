package fi.vm.sade.haku.oppija.hakemus.service;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;

public interface BaseEducationService {
    Application addBaseEducation(Application application);
    Application addSendingSchool(Application application);
}
