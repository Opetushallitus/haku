package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import fi.vm.sade.valintalaskenta.domain.valintakoe.Osallistuminen;

import java.math.BigDecimal;

public class ValintakoeDTO {

    private fi.vm.sade.valintalaskenta.domain.dto.valintakoe.ValintakoeDTO valintakoeDTO;
    private BigDecimal score;

    public ValintakoeDTO(fi.vm.sade.valintalaskenta.domain.dto.valintakoe.ValintakoeDTO valintakoeDTO) {
        this.valintakoeDTO = valintakoeDTO;
    }

    public String getNimi() {
        return valintakoeDTO.getNimi();
    }

    public String getOsallistuminen() {
        Osallistuminen osallistuminen = valintakoeDTO.getOsallistuminenTulos().getOsallistuminen();
        if (osallistuminen != null) {
            return osallistuminen.toString();
        }
        return "";
    }

    public String getValintaKoeOid() {
        return valintakoeDTO.getValintakoeOid();
    }

    public String getTunniste() {
        return valintakoeDTO.getValintakoeTunniste();
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }


}
