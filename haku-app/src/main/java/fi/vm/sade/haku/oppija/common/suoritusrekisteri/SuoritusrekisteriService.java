package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

import java.util.List;
import java.util.Map;

public interface SuoritusrekisteriService {

    Map<String, SuoritusDTO> getSuoritukset(String personOid);

    List<OpiskelijaDTO> getOpiskelijat(String personOid);

    List<ArvosanaDTO> getArvosanat(String suoritusId);
}
