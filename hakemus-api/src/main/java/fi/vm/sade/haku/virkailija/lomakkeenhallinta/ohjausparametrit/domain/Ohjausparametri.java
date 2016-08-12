package fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.domain;

import java.util.Date;

public class Ohjausparametri {
    private Date date;
    private Boolean booleanValue;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean value) {
        this.booleanValue = booleanValue;
    }

    @Override
    public String toString() {
        return "Ohjausparametri{" +
                "date=" + date +
                ", booleanValue=" + booleanValue +
                '}';
    }
}
