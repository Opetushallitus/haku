package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

import java.util.Date;

public class SuoritusDTO {

    private String oppilaitosOid;
    private Date arvioituValmistuminen;
    private String tila;
    private String luokkataso;
    private String luokka;
    private String henkiloOid;

//    {
//        "oppilaitosOid": "1.2.246.562.10.15523794103",
//            "arvioituValmistuminen": 1401829200000,
//            "tila": "KESKEN",
//            "luokkataso": "9",
//            "luokka": "9F",
//            "henkiloOid": null
//    }

    public SuoritusDTO(String oppilaitosOid, Date arvioituValmistuminen, String tila, String luokkataso, String luokka, String henkiloOid) {
        this.oppilaitosOid = oppilaitosOid;
        this.arvioituValmistuminen = arvioituValmistuminen;
        this.tila = tila;
        this.luokkataso = luokkataso;
        this.luokka = luokka;
        this.henkiloOid = henkiloOid;
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

    public Date getArvioituValmistuminen() {
        return arvioituValmistuminen;
    }

    public void setArvioituValmistuminen(Date arvioituValmistuminen) {
        this.arvioituValmistuminen = arvioituValmistuminen;
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

}
