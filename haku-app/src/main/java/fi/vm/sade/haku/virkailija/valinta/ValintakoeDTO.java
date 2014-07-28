package fi.vm.sade.haku.virkailija.valinta;

public class ValintakoeDTO {

    private String valintakoeOid;
    private String valintakoeTunniste;
    private String nimi;
    private boolean aktiivinen;
    private OsallistuminenTulosDTO osallistuminenTulos;
    private boolean lahetetaankoKoekutsut;
    private Boolean kutsutaankoKaikki;
    private Integer kutsuttavienMaara;

    public Boolean getKutsutaankoKaikki() {
        return kutsutaankoKaikki;
    }

    public void setKutsutaankoKaikki(Boolean kutsutaankoKaikki) {
        this.kutsutaankoKaikki = kutsutaankoKaikki;
    }

    public String getValintakoeOid() {
        return valintakoeOid;
    }

    public boolean isAktiivinen() {
        return aktiivinen;
    }

    public void setAktiivinen(boolean aktiivinen) {
        this.aktiivinen = aktiivinen;
    }

    public void setValintakoeOid(String valintakoeOid) {
        this.valintakoeOid = valintakoeOid;
    }

    public String getValintakoeTunniste() {
        return valintakoeTunniste;
    }

    public void setValintakoeTunniste(String valintakoeTunniste) {
        this.valintakoeTunniste = valintakoeTunniste;
    }

    public OsallistuminenTulosDTO getOsallistuminenTulos() {
        return osallistuminenTulos;
    }

    public void setOsallistuminenTulos(
            OsallistuminenTulosDTO osallistuminenTulos) {
        this.osallistuminenTulos = osallistuminenTulos;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public boolean isLahetetaankoKoekutsut() {
        return lahetetaankoKoekutsut;
    }

    public void setLahetetaankoKoekutsut(boolean lahetetaankoKoekutsut) {
        this.lahetetaankoKoekutsut = lahetetaankoKoekutsut;
    }

    public Integer getKutsuttavienMaara() {
        return kutsuttavienMaara;
    }

    public void setKutsuttavienMaara(final Integer kutsuttavienMaara) {
        this.kutsuttavienMaara = kutsuttavienMaara;
    }
}
