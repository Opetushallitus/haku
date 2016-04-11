package fi.vm.sade.haku.virkailija.valinta.dto;

import java.util.ArrayList;
import java.util.List;

public class HakutoiveDTO implements Comparable<HakutoiveDTO> {

    private Integer hakutoive;
    private String hakukohdeOid;
    private String tarjoajaOid;
    private ValintatuloksenTila vastaanottotieto;
    private List<PistetietoDTO> pistetiedot = new ArrayList<PistetietoDTO>();
    private List<HakutoiveenValintatapajonoDTO> hakutoiveenValintatapajonot = new ArrayList<HakutoiveenValintatapajonoDTO>();

    @Override
    public int compareTo(HakutoiveDTO o) {
        if (hakutoive == null) {
            return 0;
        }
        return hakutoive.compareTo(o.hakutoive);
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setTarjoajaOid(String tarjoajaOid) {
        this.tarjoajaOid = tarjoajaOid;
    }

    public String getTarjoajaOid() {
        return tarjoajaOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }

    public Integer getHakutoive() {
        return hakutoive;
    }

    public void setHakutoive(Integer hakutoive) {
        this.hakutoive = hakutoive;
    }

    public List<HakutoiveenValintatapajonoDTO> getHakutoiveenValintatapajonot() {
        return hakutoiveenValintatapajonot;
    }

    public void setHakutoiveenValintatapajonot(List<HakutoiveenValintatapajonoDTO> hakutoiveenValintatapajonot) {
        this.hakutoiveenValintatapajonot = hakutoiveenValintatapajonot;
    }

    public List<PistetietoDTO> getPistetiedot() {
        return pistetiedot;
    }

    public void setPistetiedot(List<PistetietoDTO> pistetiedot) {
        this.pistetiedot = pistetiedot;
    }

    public ValintatuloksenTila getVastaanottotieto() {
        return vastaanottotieto;
    }

    public void setVastaanottotieto(ValintatuloksenTila vastaanottotieto) {
        this.vastaanottotieto = vastaanottotieto;
    }
}
