package fi.vm.sade.haku.virkailija.valinta;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.virkailija.valinta.dto.HakemusDTO;
import fi.vm.sade.haku.virkailija.valinta.dto.HakijaDTO;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public interface ValintaService {

    HakemusDTO getHakemus(String asOid, String applicationOid);

    HakijaDTO getHakijaFromValintarekisteri(String asOid, String application);

    HakijaDTO getHakija(String asOid, String application);

    Map<String, String> fetchValintaData(Application application, Optional<Duration> valintaTimeout) throws ValintaServiceCallFailedException;
}
