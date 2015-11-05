package fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.domain;

import java.util.Date;

public class Ohjausparametri {
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "no date: " + date;
    }
}
