package fi.vm.sade.haku.virkailija.valinta.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class HakutoiveenValintatapajonoDTO {

    private Integer valintatapajonoPrioriteetti;
    private String valintatapajonoOid;
    private String valintatapajonoNimi;
    private Integer jonosija;
    private BigDecimal paasyJaSoveltuvuusKokeenTulos;
    private Integer varasijanNumero;
    private HakemuksenTila tila;
    private Map<String, String> tilanKuvaukset = new HashMap<String, String>();
    private IlmoittautumisTila ilmoittautumisTila;
    private boolean hyvaksyttyHarkinnanvaraisesti = false;
    private Integer tasasijaJonosija;
    private BigDecimal pisteet;
    private BigDecimal alinHyvaksyttyPistemaara;
    private Integer hakeneet;
    private Integer varalla;
    private Integer hyvaksytty;

    public IlmoittautumisTila getIlmoittautumisTila() {
        return ilmoittautumisTila;
    }

    public void setIlmoittautumisTila(IlmoittautumisTila ilmoittautumisTila) {
        this.ilmoittautumisTila = ilmoittautumisTila;
    }

    public String getValintatapajonoOid() {
        return valintatapajonoOid;
    }

    public void setValintatapajonoOid(String valintatapajonoOid) {
        this.valintatapajonoOid = valintatapajonoOid;
    }

    public String getValintatapajonoNimi() {
        return valintatapajonoNimi;
    }

    public void setValintatapajonoNimi(String valintatapajonoNimi) {
        this.valintatapajonoNimi = valintatapajonoNimi;
    }

    public Integer getJonosija() {
        return jonosija;
    }

    public void setJonosija(Integer jonosija) {
        this.jonosija = jonosija;
    }

    public Integer getVarasijanNumero() {
        return varasijanNumero;
    }

    public void setVarasijanNumero(Integer varasijanNumero) {
        this.varasijanNumero = varasijanNumero;
    }

    public BigDecimal getPaasyJaSoveltuvuusKokeenTulos() {
        return paasyJaSoveltuvuusKokeenTulos;
    }

    public void setPaasyJaSoveltuvuusKokeenTulos(
            BigDecimal paasyJaSoveltuvuusKokeenTulos) {
        this.paasyJaSoveltuvuusKokeenTulos = paasyJaSoveltuvuusKokeenTulos;
    }

    public HakemuksenTila getTila() {
        return tila;
    }

    public void setTila(HakemuksenTila tila) {
        this.tila = tila;
    }

    public boolean isHyvaksyttyHarkinnanvaraisesti() {
        return hyvaksyttyHarkinnanvaraisesti;
    }

    public void setHyvaksyttyHarkinnanvaraisesti(
            boolean hyvaksyttyHarkinnanvaraisesti) {
        this.hyvaksyttyHarkinnanvaraisesti = hyvaksyttyHarkinnanvaraisesti;
    }

    public Integer getTasasijaJonosija() {
        return tasasijaJonosija;
    }

    public void setTasasijaJonosija(Integer tasasijaJonosija) {
        this.tasasijaJonosija = tasasijaJonosija;
    }

    public BigDecimal getAlinHyvaksyttyPistemaara() {
        return alinHyvaksyttyPistemaara;
    }

    public void setAlinHyvaksyttyPistemaara(BigDecimal alinHyvaksyttyPistemaara) {
        this.alinHyvaksyttyPistemaara = alinHyvaksyttyPistemaara;
    }

    public Integer getVaralla() {
        return varalla;
    }

    public void setVaralla(Integer varalla) {
        this.varalla = varalla;
    }

    public Integer getHyvaksytty() {
        return hyvaksytty;
    }

    public void setHyvaksytty(Integer hyvaksytty) {
        this.hyvaksytty = hyvaksytty;
    }

    public Integer getHakeneet() {
        return hakeneet;
    }

    public void setHakeneet(Integer hakeneet) {
        this.hakeneet = hakeneet;
    }

    public BigDecimal getPisteet() {
        return pisteet;
    }

    public void setPisteet(BigDecimal pisteet) {
        this.pisteet = pisteet;
    }

    public Integer getValintatapajonoPrioriteetti() {
        return valintatapajonoPrioriteetti;
    }

    public void setValintatapajonoPrioriteetti(
            Integer valintatapajonoPrioriteetti) {
        this.valintatapajonoPrioriteetti = valintatapajonoPrioriteetti;
    }

    public Map<String, String> getTilanKuvaukset() {
        return tilanKuvaukset;
    }

    public void setTilanKuvaukset(Map<String, String> tilanKuvaukset) {
        this.tilanKuvaukset = tilanKuvaukset;
    }
}
