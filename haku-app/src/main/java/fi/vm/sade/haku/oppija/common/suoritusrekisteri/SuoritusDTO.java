package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

import java.util.Date;

public class SuoritusDTO {
//    "id":"e3599cbd-84a0-4571-8f99-6f47a18b2d92",
//            "komo":"lisaopetus",
//            "myontaja":"1.2.246.562.10.16546622305",
//            "tila":"KESKEN",
//            "valmistuminen":"30.05.2014",
//            "henkiloOid":"1.2.246.562.24.76644055995",
//            "yksilollistaminen":"Kokonaan",
//            "suoritusKieli":"KK"

    private String id;
    private String komo;
    private String myontaja;
    private String tila;
    private Date valmistuminen;
    private String henkiloOid;
    private String yksilollistaminen;
    private String suorituskieli;

    public SuoritusDTO(String id, String komo, String myontaja, String tila, Date valmistuminen, String henkiloOid,
                       String yksilollistaminen, String suorituskieli) {
        this.id = id;
        this.komo = komo;
        this.myontaja = myontaja;
        this.tila = tila;
        this.valmistuminen = valmistuminen;
        this.henkiloOid = henkiloOid;
        this.yksilollistaminen = yksilollistaminen;
        this.suorituskieli = suorituskieli;
    }

    public SuoritusDTO() {
        // NOP
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKomo() {
        return komo;
    }

    public void setKomo(String komo) {
        this.komo = komo;
    }

    public String getMyontaja() {
        return myontaja;
    }

    public void setMyontaja(String myontaja) {
        this.myontaja = myontaja;
    }

    public String getTila() {
        return tila;
    }

    public void setTila(String tila) {
        this.tila = tila;
    }

    public Date getValmistuminen() {
        return valmistuminen;
    }

    public void setValmistuminen(Date valmistuminen) {
        this.valmistuminen = valmistuminen;
    }

    public String getHenkiloOid() {
        return henkiloOid;
    }

    public void setHenkiloOid(String henkiloOid) {
        this.henkiloOid = henkiloOid;
    }

    public String getYksilollistaminen() {
        return yksilollistaminen;
    }

    public void setYksilollistaminen(String yksilollistaminen) {
        this.yksilollistaminen = yksilollistaminen;
    }

    public String getSuorituskieli() {
        return suorituskieli;
    }

    public void setSuorituskieli(String suorituskieli) {
        this.suorituskieli = suorituskieli;
    }
}
