package fi.vm.sade.haku.virkailija.valinta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ValinnanvaiheDTO {

    private int jarjestysnumero;
    private String valinnanvaiheoid;
    private String hakuOid;
    private String nimi;
    private Date createdAt;
    private List<ValintatapajonoDTO> valintatapajonot = new ArrayList<ValintatapajonoDTO>();
    private List<ValintakoeDTO> valintakokeet = new ArrayList<ValintakoeDTO>();

    public List<ValintatapajonoDTO> getValintatapajonot() {
        return valintatapajonot;
    }

    public String getValinnanvaiheoid() {
        return valinnanvaiheoid;
    }

    public void setValinnanvaiheoid(String valinnanvaiheoid) {
        this.valinnanvaiheoid = valinnanvaiheoid;
    }

    public void setValintatapajonot(List<ValintatapajonoDTO> valintatapajonot) {
        this.valintatapajonot = valintatapajonot;
    }

    public int getJarjestysnumero() {
        return jarjestysnumero;
    }

    public void setJarjestysnumero(int jarjestysnumero) {
        this.jarjestysnumero = jarjestysnumero;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public List<ValintakoeDTO> getValintakokeet() {
        return valintakokeet;
    }

    public void setValintakokeet(List<ValintakoeDTO> valintakokeet) {
        this.valintakokeet = valintakokeet;
    }

    public String getHakuOid() {
        return hakuOid;
    }

    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }
}
