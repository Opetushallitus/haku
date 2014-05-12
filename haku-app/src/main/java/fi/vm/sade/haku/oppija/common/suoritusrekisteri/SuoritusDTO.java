package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

import java.util.Date;

public class SuoritusDTO {

    private Date valmistuminen;
    private String id;
    private String tila;
    private String henkiloOid;
    private String suorituskieli;
    private Integer pohjakoulutus;
    private String luokkataso;

    public SuoritusDTO(String id, Date valmistuminen, String tila, String henkiloOid, Integer pohjakoulutus, String suorituskieli) {
        this.id = id;
        this.valmistuminen = valmistuminen;
        this.tila = tila;
        this.henkiloOid = henkiloOid;
        this.pohjakoulutus = pohjakoulutus;
        this.suorituskieli = suorituskieli;
    }

    public SuoritusDTO() {
        // Empty
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getValmistuminen() {
        return valmistuminen;
    }

    public void setValmistuminen(Date valmistuminen) {
        this.valmistuminen = valmistuminen;
    }

    public String getTila() {
        return tila;
    }

    public void setTila(String tila) {
        this.tila = tila;
    }

    public String getHenkiloOid() {
        return henkiloOid;
    }

    public void setHenkiloOid(String henkiloOid) {
        this.henkiloOid = henkiloOid;
    }

    public void setSuorituskieli(String suorituskieli) {
        this.suorituskieli = suorituskieli;
    }

    public String getSuorituskieli() {
        return suorituskieli;
    }

    public void setPohjakoulutus(Integer pohjakoulutus) {
        this.pohjakoulutus = pohjakoulutus;
    }

    public Integer getPohjakoulutus() {
        return pohjakoulutus;
    }


    public void setLuokkataso(String luokkataso) {
        this.luokkataso = luokkataso;
    }

    public String getLuokkataso() {
        return luokkataso;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append("{")
                .append(" id : ").append(id)
                .append(" valmistuminen : ").append(valmistuminen)
                .append(" tila : ").append(tila)
                .append(" henkiloOid : ").append(henkiloOid)
                .append(" suorituskieli : ").append(suorituskieli)
                .append(" pohjakoulutus : ").append(pohjakoulutus)
                .append(" luokkataso : ").append(luokkataso)
                .append("}");
        return builder.toString();
    }
}
