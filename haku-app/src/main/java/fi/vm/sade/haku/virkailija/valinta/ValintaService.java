package fi.vm.sade.haku.virkailija.valinta;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationOptionDTO;

import java.util.List;

public interface ValintaService {

    List<ApplicationOptionDTO> getValintakoeOsallistuminen(Application application);

}
