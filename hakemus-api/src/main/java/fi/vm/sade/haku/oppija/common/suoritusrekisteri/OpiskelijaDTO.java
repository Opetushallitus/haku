package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

import java.util.Date;

public class OpiskelijaDTO {

    private String id;
    private String oppilaitosOid;
    private String luokkataso;
    private String luokka;
    private String henkiloOid;
    private Date alkuPaiva;
    private Date loppuPaiva;
    private String source;

//    {
//        "id":"ca87d0df-1fa6-41f8-9bb0-949fa22a1960",
//        "oppilaitosOid":"1.2.246.562.10.16546622305",
//        "luokkataso":"A",
//        "luokka":"10A",
//        "henkiloOid":"1.2.246.562.24.62464199838",
//        "alkuPaiva":"2014-07-31T21:00:00.000Z",
//        "loppuPaiva":"2015-05-31T21:00:00.000Z",
//        "source":"1.2.246.562.24.72453542949",
//    }

    public OpiskelijaDTO(String id, String oppilaitosOid, String luokkataso, String luokka, String henkiloOid, Date alkuPaiva, Date loppuPaiva, String source) {
        this.id = id;
        this.oppilaitosOid = oppilaitosOid;
        this.luokkataso = luokkataso;
        this.luokka = luokka;
        this.henkiloOid = henkiloOid;
        this.alkuPaiva = alkuPaiva;
        this.loppuPaiva = loppuPaiva;
        this.source = source;
    }

    public OpiskelijaDTO() {
        // NOP
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getAlkuPaiva() {
        return alkuPaiva;
    }

    public void setAlkuPaiva(Date alkuPaiva) {
        this.alkuPaiva = alkuPaiva;
    }

    public Date getLoppuPaiva() {
        return loppuPaiva;
    }

    public void setLoppuPaiva(Date loppuPaiva) {
        this.loppuPaiva = loppuPaiva;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
