package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

import java.util.List;
import java.util.Map;

public interface SuoritusrekisteriService {

    /**
     * Palauttaa henkilön suoritukset mappina komoOid - suoritus.
     * @param personOid
     * @return suoritukset mappina komoOid - suoritus
     */
    Map<String, SuoritusDTO> getSuoritukset(String personOid);

    List<OpiskelijaDTO> getOpiskelijat(String personOid);

    List<ArvosanaDTO> getArvosanat(String suoritusId);
}
