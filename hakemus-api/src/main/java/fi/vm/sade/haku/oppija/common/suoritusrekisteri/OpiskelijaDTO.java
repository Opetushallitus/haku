package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

import java.util.Date;

public class OpiskelijaDTO {

    private String oppilaitosOid;
    private String luokkataso;
    private String luokka;
    private String henkiloOid;
    private Date loppuPaiva;

//    {
//        "oppilaitosOid": "1.2.246.562.10.15523794103",
//            "luokkataso": "9",
//            "luokka": "9C",
//            "henkiloOid": "1.2.3"
//    }

    public OpiskelijaDTO(String oppilaitosOid, String luokkataso, String luokka, String henkiloOid,
                         Date loppuPaiva) {
        this.oppilaitosOid = oppilaitosOid;
        this.luokkataso = luokkataso;
        this.luokka = luokka;
        this.henkiloOid = henkiloOid;
        this.loppuPaiva = loppuPaiva;
    }

    public OpiskelijaDTO() {
        // Empty
    }

    public String getOppilaitosOid() {
        return oppilaitosOid;
    }

    public void setOppilaitosOid(String oppilaitosOid) {
        this.oppilaitosOid = oppilaitosOid;
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

    public void setLoppuPaiva(Date loppuPaiva) {
        this.loppuPaiva = loppuPaiva;
    }

    public Date getLoppuPaiva() {
        return loppuPaiva;
    }
}
