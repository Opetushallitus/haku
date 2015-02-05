package fi.vm.sade.haku.oppija.common.koulutusinformaatio.impl;

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOption;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(value = {"dev", "it"})
public class ApplicationOptionServiceMockImpl implements ApplicationOptionService {
    @Override
    public ApplicationOption get(String oid) {
        ApplicationOption ao = new ApplicationOption();
        ao.setId(oid);
        return ao;
    }

    @Override
    public ApplicationOption get(String oid, String lang) {
        return get(oid);
    }
}
