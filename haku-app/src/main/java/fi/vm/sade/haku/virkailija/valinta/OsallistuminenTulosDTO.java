package fi.vm.sade.haku.virkailija.valinta;

import java.util.Map;

public class OsallistuminenTulosDTO {
    private Osallistuminen osallistuminen;
    private Map<String,String> kuvaus;
    private String laskentaTila;
    private Boolean laskentaTulos;

    public Osallistuminen getOsallistuminen() {
        return osallistuminen;
    }

    public void setOsallistuminen(Osallistuminen osallistuminen) {
        this.osallistuminen = osallistuminen;
    }

    public String getLaskentaTila() {
        return laskentaTila;
    }

    public void setLaskentaTila(String laskentaTila) {
        this.laskentaTila = laskentaTila;
    }

    public Boolean getLaskentaTulos() {
        return laskentaTulos;
    }

    public void setLaskentaTulos(Boolean laskentaTulos) {
        this.laskentaTulos = laskentaTulos;
    }

    public Map<String, String> getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(Map<String, String> kuvaus) {
        this.kuvaus = kuvaus;
    }
}
