package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class SyntheticApplication {

    private String hakukohdeOid;
    private String hakuOid;
    private List<Person> hakemukset;

    @JsonCreator
    public SyntheticApplication(@JsonProperty(value = "hakukohdeOid") String hakukohdeOid,
                                @JsonProperty(value = "hakuOid") String hakuOid,
                                @JsonProperty(value = "hakemukset") List<Person> hakemukset) {

        this.hakukohdeOid = hakukohdeOid;
        this.hakemukset = hakemukset;
        this.hakuOid = hakuOid;
    }

    public List<Person> getHakemukset() {
        return hakemukset;
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public String getHakuOid() {
        return hakuOid;
    }

    public static class Person {

        private String hakijaOid;
        private String etunimi;
        private String sukunimi;
        private String hetu;

        public String getHakijaOid() {
            return hakijaOid;
        }

        public String getEtunimi() {
            return etunimi;
        }

        public String getSukunimi() {
            return sukunimi;
        }

        public String getHetu() {
            return hetu;
        }
    }
}

