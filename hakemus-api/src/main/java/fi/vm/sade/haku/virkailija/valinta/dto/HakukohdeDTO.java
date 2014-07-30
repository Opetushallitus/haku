package fi.vm.sade.haku.virkailija.valinta.dto;

import java.util.ArrayList;
import java.util.List;

public class HakukohdeDTO {
    private String hakuoid;
    private String tarjoajaoid;
    private String hakukohdeoid;
    private List<ValinnanvaiheDTO> valinnanvaihe = new ArrayList<ValinnanvaiheDTO>();

    public String getHakuoid() {
        return hakuoid;
    }

    public void setHakuoid(String hakuoid) {
        this.hakuoid = hakuoid;
    }

    public String getOid() {
        return hakukohdeoid;
    }

    public void setOid(String oid) {
        this.hakukohdeoid = oid;
    }

    public List<ValinnanvaiheDTO> getValinnanvaihe() {
        return valinnanvaihe;
    }

    public void setValinnanvaihe(List<ValinnanvaiheDTO> valinnanvaihe) {
        this.valinnanvaihe = valinnanvaihe;
    }

    public String getTarjoajaoid() {
        return tarjoajaoid;
    }

    public void setTarjoajaoid(String tarjoajaoid) {
        this.tarjoajaoid = tarjoajaoid;
    }
}