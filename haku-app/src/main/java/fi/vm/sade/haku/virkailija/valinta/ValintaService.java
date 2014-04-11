package fi.vm.sade.haku.virkailija.valinta;

import fi.vm.sade.sijoittelu.tulos.dto.raportointi.HakijaDTO;
import fi.vm.sade.valintalaskenta.domain.dto.HakemusDTO;

public interface ValintaService {

    HakemusDTO getHakemus(String asOid, String applicationOid);

    HakijaDTO getHakija(String asOid, String application);
}
