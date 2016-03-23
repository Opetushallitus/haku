package fi.vm.sade.haku.virkailija.valinta;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.virkailija.valinta.dto.HakemusDTO;
import fi.vm.sade.haku.virkailija.valinta.dto.HakijaDTO;

import java.util.Map;

public interface ValintaService {

    HakemusDTO getHakemus(String asOid, String applicationOid);

    HakijaDTO getHakija(String asOid, String application);

    Map<String, String> fetchValintaData(Application application) throws ValintaServiceCallFailedException;
}
