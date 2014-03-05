package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import java.util.ArrayList;
import java.util.List;

public class ApplicationOptionDTO {

    private String oid;
    private String name;
    private String opetuspiste;
    private String opetuspisteOid;
    private List<ValintakoeDTO> tests = new ArrayList<ValintakoeDTO>();

    private Double totalScore;
    private String sijoittelunTulos;
    private String hylkayksenSyy;
    private String vastaanottoTieto;
    private String ilmoittautuminen;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpetuspiste() {
        return opetuspiste;
    }

    public void setOpetuspiste(String opetuspiste) {
        this.opetuspiste = opetuspiste;
    }

    public String getOpetuspisteOid() {
        return opetuspisteOid;
    }

    public void setOpetuspisteOid(String opetuspisteOid) {
        this.opetuspisteOid = opetuspisteOid;
    }

    public void addTest(ValintakoeDTO test) {
        tests.add(test);
    }

    public void setTest(List<ValintakoeDTO> tests) {
        this.tests = tests;
    }

    public List<ValintakoeDTO> getTests() {
        return tests;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public String getSijoittelunTulos() {
        return sijoittelunTulos;
    }

    public void setSijoittelunTulos(String sijoittelunTulos) {
        this.sijoittelunTulos = sijoittelunTulos;
    }

    public String getHylkayksenSyy() {
        return hylkayksenSyy;
    }

    public void setHylkayksenSyy(String hylkayksenSyy) {
        this.hylkayksenSyy = hylkayksenSyy;
    }

    public String getVastaanottoTieto() {
        return vastaanottoTieto;
    }

    public void setVastaanottoTieto(String vastaanottoTieto) {
        this.vastaanottoTieto = vastaanottoTieto;
    }

    public String getIlmoittautuminen() {
        return ilmoittautuminen;
    }

    public void setIlmoittautuminen(String ilmoittautuminen) {
        this.ilmoittautuminen = ilmoittautuminen;
    }
}
