package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

import java.util.Date;

public class SuoritusDTO {

    public static final String TILA_KESKEN = "KESKEN";
    public static final String TILA_KESKEYTYNYT = "KESKEYTYNYT";
    public static final String TILA_VALMIS = "VALMIS";

//    {
//        "id": "bfe621f5-68f7-49cf-9dad-9e3651f3ee03"
//        "komo": "1.2.246.562.13.62959769647",
//        "myontaja": "1.2.246.562.24.28128709553",
//        "tila": "VALMIS",
//        "valmistuminen": "31.05.2014",
//        "henkiloOid": "1.2.246.562.24.28128709553",
//        "yksilollistaminen": "Ei",
//        "suoritusKieli": "XX",
//        "source": "1.2.246.562.24.28128709553",
//        "vahvistettu": true,
//    }

    private String id;
    private String komo;
    private String myontaja;
    private String tila;
    private Date valmistuminen;
    private String henkiloOid;
    private String yksilollistaminen;
    private String suoritusKieli;
    private String source;
    private Boolean vahvistettu;

    public SuoritusDTO(String id, String komo, String myontaja, String tila, Date valmistuminen, String henkiloOid,
                       String yksilollistaminen, String suoritusKieli, String source, Boolean vahvistettu) {
        this.id = id;
        this.komo = komo;
        this.myontaja = myontaja;
        this.tila = tila;
        this.valmistuminen = valmistuminen;
        this.henkiloOid = henkiloOid;
        this.yksilollistaminen = yksilollistaminen;
        this.suoritusKieli = suoritusKieli;
        this.source = source;
        this.vahvistettu = vahvistettu;
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

    public String getSuoritusKieli() {
        return suoritusKieli;
    }

    public void setSuoritusKieli(String suorituskieli) {
        this.suoritusKieli = suorituskieli;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getVahvistettu() {
        return vahvistettu;
    }

    public void setVahvistettu(Boolean vahvistettu) {
        this.vahvistettu = vahvistettu;
    }
}
