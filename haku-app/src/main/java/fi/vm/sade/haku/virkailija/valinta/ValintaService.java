package fi.vm.sade.haku.virkailija.valinta;

import fi.vm.sade.haku.virkailija.valinta.dto.HakemusDTO;
import fi.vm.sade.haku.virkailija.valinta.dto.HakijaDTO;

public interface ValintaService {

    HakemusDTO getHakemus(String asOid, String applicationOid);

    HakijaDTO getHakija(String asOid, String application);
}
