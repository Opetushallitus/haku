package fi.vm.sade.haku.virkailija.valinta;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.sijoittelu.tulos.dto.raportointi.HakijaDTO;

import java.util.List;

public interface ValintaService {

    List<ApplicationOptionDTO> getValintakoeOsallistuminen(Application application);

    HakijaDTO getHakija(String asOid, String application);
}
