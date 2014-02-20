package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

import java.util.Date;

public class SuoritusDTO {

    private String oppilaitosOid;
    private Date valmistuminen;
    private String tila;
    private String luokkataso;
    private String luokka;
    private String henkiloOid;
    private String suorituskieli;
    private Integer pohjakoulutus;

//    {
//        "oppilaitosOid": "1.2.246.562.10.15523794103",
//            "valmistuminen": 1401829200000,
//            "tila": "KESKEN",
//            "luokkataso": "9",
//            "luokka": "9F",
//            "henkiloOid": null
//    }

    public SuoritusDTO(String oppilaitosOid, Date valmistuminen, String tila, String luokkataso, String luokka,
                       String henkiloOid, Integer pohjakoulutus, String suorituskieli) {
        this.oppilaitosOid = oppilaitosOid;
        this.valmistuminen = valmistuminen;
        this.tila = tila;
        this.luokkataso = luokkataso;
        this.luokka = luokka;
        this.henkiloOid = henkiloOid;
        this.pohjakoulutus = pohjakoulutus;
        this.suorituskieli = suorituskieli;
    }

    public SuoritusDTO() {
        // Empty
    }

    public String getOppilaitosOid() {
        return oppilaitosOid;
    }

    public void setOppilaitosOid(String oppilaitosOid) {
        this.oppilaitosOid = oppilaitosOid;
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

    public String getLuokkataso() {
        return luokkataso;
    }

    public void setLuokkataso(String luokkataso) {
        this.luokkataso = luokkataso;
    }

    public String getLuokka() {
        return luokka;
    }

    public void setLuokka(String luokka) {
        this.luokka = luokka;
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
}
