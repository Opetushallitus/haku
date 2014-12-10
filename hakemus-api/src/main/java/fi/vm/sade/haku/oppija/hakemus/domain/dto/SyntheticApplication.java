package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class SyntheticApplication {

    public final String hakukohdeOid;
    public final String hakuOid;
    public final String tarjoajaOid;
    public final List<Hakemus> hakemukset;

    @JsonCreator
    public SyntheticApplication(@JsonProperty(value = "hakukohdeOid") String hakukohdeOid,
                                @JsonProperty(value = "hakuOid") String hakuOid,
                                @JsonProperty(value = "tarjoajaOid") String tarjoajaOid,
                                @JsonProperty(value = "hakemukset") List<Hakemus> hakemukset) {

        this.hakukohdeOid = hakukohdeOid;
        this.hakemukset = hakemukset;
        this.tarjoajaOid = tarjoajaOid;
        this.hakuOid = hakuOid;
    }

    public static class Hakemus {

        public final String hakijaOid;
        public final String etunimi;
        public final String sukunimi;
        public final String henkilotunnus;
        public final String syntymaAika;

        @JsonCreator
        public Hakemus(@JsonProperty("hakijaOid") String hakijaOid,
                       @JsonProperty("etunimi") String etunimi,
                       @JsonProperty("sukunimi") String sukunimi,
                       @JsonProperty("henkilotunnus") String henkilotunnus,
                       @JsonProperty("syntymaAika") String syntymaAika) {
            this.hakijaOid = hakijaOid;
            this.etunimi = etunimi;
            this.sukunimi = sukunimi;
            this.henkilotunnus = henkilotunnus;
            this.syntymaAika = syntymaAika;
        }
    }
}

