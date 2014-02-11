package fi.vm.sade.haku.oppija.common.suoritusrekisteri.impl;

import fi.vm.sade.haku.oppija.common.suoritusrekisteri.OpiskelijaDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusDTO;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Profile(value = {"dev", "it"})
public class SuoritusrekisteriServiceMockImpl implements SuoritusrekisteriService {

    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

    @Override
    public List<SuoritusDTO> getSuoritukset(String personOid, String hakuvuosi, String hakukausi) {
        List<SuoritusDTO> suoritukset = new ArrayList<SuoritusDTO>(1);
        Date tomorrow = new Date(System.currentTimeMillis() + ONE_DAY);
        SuoritusDTO suoritus = new SuoritusDTO("1.2.246.562.10.43523490233", tomorrow, "KESKEN", "9", "9A", personOid);
        suoritukset.add(suoritus);
        return suoritukset;
    }

    @Override
    public List<OpiskelijaDTO> getOpiskelijat(String personOid) {
        List<OpiskelijaDTO> suoritukset = new ArrayList<OpiskelijaDTO>(1);
        OpiskelijaDTO opiskelija = new OpiskelijaDTO("1.2.246.562.10.43523490233", "9", "9A", personOid);
        suoritukset.add(opiskelija);
        return suoritukset;
    }
}
