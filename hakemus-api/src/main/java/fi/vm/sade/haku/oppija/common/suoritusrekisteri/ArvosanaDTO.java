package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

import java.util.Date;

public class ArvosanaDTO {

    private String id;
    private String suoritus;
    private ArvioDTO arvio;
    private String aine;
    // Organisaatio (oppilaitoksen siirtämät) tai hakemus (hakijan syöttämät)
    private String source;
    private Boolean valinnainen;
    // Kieli
    private String lisatieto;
    private Date myonnetty;
    // Valinnaisten aineiden järjestys. Ei ole mukana yhteisillä aineilla.
    private Integer jarjestys;

//    {
//        "id": "18b1b239-d28a-43f7-a251-78e55f780866",
//        "suoritus": "fc77b0f4-3fb7-41b2-b4bd-51ebabdacfb4",
//        "arvio": {
//            "arvosana": "9",
//            "asteikko": "4-10"
//        },
//        "aine": "A1",
//        "source": "1.2.246.562.24.69423911513",
//        "valinnainen": true,
//        "lisatieto": "EN",
//        "myonnetty": "01.01.2015",
//        "jarjestys": 1
//    },

    public ArvosanaDTO(String id, String suoritus, ArvioDTO arvio, String aine, String source, Boolean valinnainen, String lisatieto, Date myonnetty, Integer jarjestys) {
        this.id = id;
        this.suoritus = suoritus;
        this.arvio = arvio;
        this.aine = aine;
        this.source = source;
        this.valinnainen = valinnainen;
        this.lisatieto = lisatieto;
        this.myonnetty = myonnetty;
        this.jarjestys = jarjestys;
    }

    public ArvosanaDTO() {
        // NOP
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSuoritus() {
        return suoritus;
    }

    public void setSuoritus(String suoritus) {
        this.suoritus = suoritus;
    }

    public ArvioDTO getArvio() {
        return arvio;
    }

    public void setArvio(ArvioDTO arvio) {
        this.arvio = arvio;
    }

    public String getAine() {
        return aine;
    }

    public void setAine(String aine) {
        this.aine = aine;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getValinnainen() {
        return valinnainen;
    }

    public boolean isValinnainen() {
        return valinnainen != null ? valinnainen.booleanValue() : false;
    }

    public void setValinnainen(Boolean valinnainen) {
        this.valinnainen = valinnainen;
    }

    public String getLisatieto() {
        return lisatieto;
    }

    public void setLisatieto(String lisatieto) {
        this.lisatieto = lisatieto;
    }

    public Date getMyonnetty() {
        return myonnetty;
    }

    public void setMyonnetty(Date myonnetty) {
        this.myonnetty = myonnetty;
    }

    public Integer getJarjestys() {
        return jarjestys;
    }

    public void setJarjestys(Integer jarjestys) {
        this.jarjestys = jarjestys;
    }
}
