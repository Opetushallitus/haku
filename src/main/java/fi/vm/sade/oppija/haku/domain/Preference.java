package fi.vm.sade.oppija.haku.domain;

/**
 * @author jukka
 * @version 10/18/1210:33 AM}
 * @since 1.1
 */
public class Preference {

    private final String koulutus;

    private final String koulutusId;
    private final Integer order;
    private final String opetusPiste;
    private final String opetusPisteId;

    public Preference(Integer order, String opetusPiste, String opetusPisteId, String koulutus, String koulutusId) {
        this.order = order;
        this.opetusPiste = opetusPiste;
        this.opetusPisteId = opetusPisteId;
        this.koulutus = koulutus;
        this.koulutusId = koulutusId;
    }

    public String getKoulutus() {
        return koulutus;
    }

    public String getKoulutusId() {
        return koulutusId;
    }

    public String getOpetusPiste() {
        return opetusPiste;
    }

    public String getOpetusPisteId() {
        return opetusPiste;
    }


    public Integer getOrder() {
        return order;
    }
}
