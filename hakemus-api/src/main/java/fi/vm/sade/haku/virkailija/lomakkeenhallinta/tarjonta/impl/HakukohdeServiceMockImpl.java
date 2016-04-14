package fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile(value = {"dev", "it"})
public class HakukohdeServiceMockImpl implements HakukohdeService {
    @Override
    public HakukohdeV1RDTO findByOid(String oid) {
        return null;
    }

    @Override
    public List<String> findByGroupAndApplicationSystem(String applicationOptionGroupId, String applicationSystemId) {
        return Lists.newArrayList("1.2", "3.4", "5.6");
    }
}