package fi.vm.sade.haku.virkailija.valinta.dto;

public interface ValintaService {

    HakemusDTO getHakemus(String asOid, String applicationOid);

    HakijaDTO getHakija(String asOid, String application);
}
