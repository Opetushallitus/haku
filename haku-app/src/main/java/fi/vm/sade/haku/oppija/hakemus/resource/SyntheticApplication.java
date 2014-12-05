package fi.vm.sade.haku.oppija.hakemus.resource;


import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SyntheticApplication {

    private String hakukohdeOid;
    private String hakijaOid;
    private String etunimi;
    private String sukunimi;
    private String hakuOid;

    @JsonCreator
    public SyntheticApplication(@JsonProperty(value = "hakukohdeOid") String hakukohdeOid,
                                @JsonProperty(value = "hakijaOid") String hakijaOid,
                                @JsonProperty(value = "hakuOid") String hakuOid,
                                @JsonProperty(value = "etunimi") String etunimi,
                                @JsonProperty(value = "sukunimi") String sukunimi) {

        this.hakukohdeOid = hakukohdeOid;
        this.hakijaOid = hakijaOid;
        this.hakuOid = hakuOid;
        this.etunimi = etunimi;
        this.sukunimi = sukunimi;
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }

    public String getHakijaOid() {
        return hakijaOid;
    }

    public void setHakijaOid(String hakijaOid) {
        this.hakijaOid = hakijaOid;
    }

    public String getEtunimi() {
        return etunimi;
    }

    public void setEtunimi(String etunimi) {
        this.etunimi = etunimi;
    }

    public String getSukunimi() {
        return sukunimi;
    }

    public void setSukunimi(String sukunimi) {
        this.sukunimi = sukunimi;
    }

    public String getHakuOid() {
        return hakuOid;
    }

    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }
}
