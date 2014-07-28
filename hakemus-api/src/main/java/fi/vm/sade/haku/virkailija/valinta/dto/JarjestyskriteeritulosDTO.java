package fi.vm.sade.haku.virkailija.valinta.dto;

import java.math.BigDecimal;
import java.util.Map;

public class JarjestyskriteeritulosDTO implements
        Comparable<JarjestyskriteeritulosDTO> {

    private BigDecimal arvo;
    private JarjestyskriteerituloksenTila tila;
    private Map<String, String> kuvaus;
    private int prioriteetti;
    private String nimi;

    @Override
    public int compareTo(JarjestyskriteeritulosDTO o) {
        return Integer.valueOf(prioriteetti).compareTo(o.getPrioriteetti());
    }

    public BigDecimal getArvo() {
        return arvo;
    }

    public void setArvo(BigDecimal arvo) {
        this.arvo = arvo;
    }

    public JarjestyskriteerituloksenTila getTila() {
        return tila;
    }

    public void setTila(JarjestyskriteerituloksenTila tila) {
        this.tila = tila;
    }

    public int getPrioriteetti() {
        return prioriteetti;
    }

    public void setPrioriteetti(int prioriteetti) {
        this.prioriteetti = prioriteetti;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public Map<String, String> getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(Map<String, String> kuvaus) {
        this.kuvaus = kuvaus;
    }
}
