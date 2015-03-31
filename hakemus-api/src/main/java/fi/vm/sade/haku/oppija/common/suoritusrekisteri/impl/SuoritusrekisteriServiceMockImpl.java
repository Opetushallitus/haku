package fi.vm.sade.haku.oppija.common.suoritusrekisteri.impl;

import fi.vm.sade.haku.oppija.common.suoritusrekisteri.ArvosanaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Profile(value = {"dev", "it"})
public class SuoritusrekisteriServiceMockImpl implements SuoritusrekisteriService {

    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

    @Override
    public Map<String, List<SuoritusDTO>> getSuoritukset(String personOid) {
        Map<String, List<SuoritusDTO>> suoritukset = new HashMap<>(1);
        Date tomorrow = new Date(System.currentTimeMillis() + ONE_DAY);
        final SuoritusDTO suoritus = new SuoritusDTO("suoritusId", "1.2.246.562.13.62959769647", "myontaja", "KESKEN",
                tomorrow, personOid, "Ei", "FI", "source", true);

        suoritukset.put("1.2.246.562.13.62959769647", new ArrayList<SuoritusDTO>() {{ add(suoritus); }});
        return suoritukset;
    }

    @Override
    public List<OpiskelijaDTO> getOpiskelijatiedot(String personOid) {
        List<OpiskelijaDTO> suoritukset = new ArrayList<>(1);
        OpiskelijaDTO opiskelija = new OpiskelijaDTO("opiskelijaId", "oppilaitos", "9", "9A", personOid, yesterday(), tomorrow(), "source");
        suoritukset.add(opiskelija);
        return suoritukset;
    }

    @Override
    public List<ArvosanaDTO> getArvosanat(String suoritusId) {
        return new ArrayList<>();
    }

    @Override
    public Map<String, List<SuoritusDTO>> getSuoritukset(String personOid, String komoOid) {
        return getSuoritukset(personOid);
    }

    private Date tomorrow() {
        return new Date(System.currentTimeMillis() + ONE_DAY);
    }
    private Date yesterday() {
        return new Date(System.currentTimeMillis() - ONE_DAY);
    }
}
