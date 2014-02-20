package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

import java.util.List;

public interface SuoritusrekisteriService {

    List<SuoritusDTO> getSuoritukset(String personOid);

    List<OpiskelijaDTO> getOpiskelijat(String personOid);
}
