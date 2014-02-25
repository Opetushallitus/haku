package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

import java.util.Date;

public class SuoritusDTO {

    private Date valmistuminen;
    private String tila;
    private String henkiloOid;
    private String suorituskieli;
    private Integer pohjakoulutus;

    public SuoritusDTO(Date valmistuminen, String tila, String henkiloOid, Integer pohjakoulutus, String suorituskieli) {
        this.valmistuminen = valmistuminen;
        this.tila = tila;
        this.henkiloOid = henkiloOid;
        this.pohjakoulutus = pohjakoulutus;
        this.suorituskieli = suorituskieli;
    }

    public SuoritusDTO() {
        // Empty
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

    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append("{")
                .append(" valmistuminen :").append(valmistuminen)
                .append(" tila :").append(tila)
                .append(" henkiloOid :").append(henkiloOid)
                .append(" suorituskieli :").append(suorituskieli)
                .append(" pohjakoulutus :").append(pohjakoulutus);
        return builder.toString();
    }
}
