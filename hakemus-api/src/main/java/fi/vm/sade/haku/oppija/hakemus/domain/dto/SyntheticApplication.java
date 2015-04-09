package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
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
        public final String sukupuoli;
        public final String aidinkieli;
        public final String sukunimi;
        public final String henkilotunnus;
        public final String sahkoposti;
        public final String syntymaAika;

        @JsonCreator
        public Hakemus(@JsonProperty("hakijaOid") String hakijaOid,
                       @JsonProperty("etunimi") String etunimi,
                       @JsonProperty("sukunimi") String sukunimi,
                       @JsonProperty("sukupuoli") String sukupuoli,
                       @JsonProperty("aidinkieli") String aidinkieli,
                       @JsonProperty("henkilotunnus") String henkilotunnus,
                       @JsonProperty("sahkoposti") String sahkoposti,
                       @JsonProperty("syntymaAika") String syntymaAika) {
            this.hakijaOid = hakijaOid;
            this.etunimi = etunimi;
            this.sukupuoli = sukupuoli;
            this.aidinkieli = aidinkieli;
            this.sukunimi = sukunimi;
            this.henkilotunnus = henkilotunnus;
            this.sahkoposti = sahkoposti;
            this.syntymaAika = syntymaAika;
        }
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);

    }
}

