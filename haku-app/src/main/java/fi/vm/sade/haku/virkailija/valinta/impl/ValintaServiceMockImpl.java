package fi.vm.sade.haku.virkailija.valinta.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ValintakoeDTO;
import fi.vm.sade.haku.virkailija.valinta.ValintaService;
import fi.vm.sade.valintalaskenta.domain.dto.valintakoe.OsallistuminenTulosDTO;
import fi.vm.sade.valintalaskenta.domain.valintakoe.Osallistuminen;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Profile(value = {"dev", "it"})
public class ValintaServiceMockImpl implements ValintaService {

    @Override
    public List<ApplicationOptionDTO> getValintakoeOsallistuminen(Application application) {
        ApplicationOptionDTO ao = new ApplicationOptionDTO();
        ao.setName("Hakukohteen nimi");
        ao.setOid("1.2.3.4");
        ao.setOpetuspiste("Koulu");
        ao.setOpetuspisteOid("1.2.3.4");

        ValintakoeDTO test = createValintakoe();
        ao.setTest(Lists.newArrayList(test));

        ao.setTotalScore(4.5);
        ao.setSijoittelunTulos("HYVÄKSYTTY");
        ao.setHylkayksenSyy("");
        ao.setVastaanottoTieto("PAIKKA VASTAANOTETTU");
        ao.setIlmoittautuminen("EI ILMOITTAUTUNUT");
        return Lists.newArrayList(ao, ao);
    }

    private ValintakoeDTO createValintakoe() {
        fi.vm.sade.valintalaskenta.domain.dto.valintakoe.ValintakoeDTO koe = new fi.vm.sade.valintalaskenta.domain.dto.valintakoe.ValintakoeDTO();
        koe.setAktiivinen(true);
        koe.setNimi("Valintakoe");
        OsallistuminenTulosDTO osallistuminenTulos = new OsallistuminenTulosDTO();
        osallistuminenTulos.setLaskentaTila("Kesken");
        osallistuminenTulos.setLaskentaTulos(Boolean.TRUE);
        osallistuminenTulos.setOsallistuminen(Osallistuminen.OSALLISTUU);
        koe.setOsallistuminenTulos(osallistuminenTulos);

        ValintakoeDTO test = new ValintakoeDTO(koe);
        test.setScore(new BigDecimal(4));
        return test;
    }

}
