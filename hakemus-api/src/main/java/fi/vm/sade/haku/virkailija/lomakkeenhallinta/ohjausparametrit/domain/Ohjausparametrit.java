package fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class Ohjausparametrit {
    @JsonProperty("PH_AHP")
    private Ohjausparametri PH_AHP;
    @JsonProperty("PH_KVT")
    private Ohjausparametri PH_KVT;

    public Ohjausparametri getPH_AHP() {
        return PH_AHP;
    }

    public void setPH_AHP(Ohjausparametri PH_AHP) {
        this.PH_AHP = PH_AHP;
    }

    public Ohjausparametri getPH_KVT() {
        return PH_KVT;
    }

    public void setPH_KVT(Ohjausparametri PH_KVT) {
        this.PH_KVT = PH_KVT;
    }

    @Override
    public String toString() {
        return "Ohjausparametrit(PH_AHP="+ PH_AHP +", PH_KVT=" + PH_KVT + ")";
    }
}
