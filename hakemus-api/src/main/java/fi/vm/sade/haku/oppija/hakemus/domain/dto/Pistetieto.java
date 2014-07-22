package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.sijoittelu.tulos.dto.PistetietoDTO;
import fi.vm.sade.valintalaskenta.domain.valintakoe.Osallistuminen;

import java.util.HashMap;
import java.util.Map;

public class Pistetieto {

    private String id;
    private I18nText nimi;
    private Osallistuminen osallistuminen;
    private I18nText osallistuminenText;
    private String pisteet;

    private static final String BUNDLE_NAME = "messages";
    private static final Map<Osallistuminen, I18nText> osallistuminenTranslations;

    static {
        osallistuminenTranslations = new HashMap<Osallistuminen, I18nText>(3);
        osallistuminenTranslations.put(Osallistuminen.OSALLISTUU,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.osallistuminen.osallistuu", BUNDLE_NAME));
        osallistuminenTranslations.put(Osallistuminen.EI_OSALLISTU,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.osallistuminen.eiOsallistu", BUNDLE_NAME));
        osallistuminenTranslations.put(Osallistuminen.VIRHE,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.osallistuminen.virhe", BUNDLE_NAME));
    }

    public Pistetieto() {

    }

    public Pistetieto(PistetietoDTO pistetietoDTO) {
        this.id = pistetietoDTO.getTunniste();
        this.pisteet = pistetietoDTO.getLaskennallinenArvo();
        this.osallistuminenText = ElementUtil.createI18NAsIs(pistetietoDTO.getOsallistuminen());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nText getNimi() {
        return nimi;
    }

    public void setNimi(I18nText nimi) {
        this.nimi = nimi;
    }

    public I18nText getOsallistuminenText() {
        return osallistuminenText;
    }

    public Osallistuminen getOsallistuminen() {
        return osallistuminen;
    }

    public void setOsallistuminen(Osallistuminen osallistuminen) {
        this.osallistuminen = osallistuminen;
        if (osallistuminenTranslations.containsKey(osallistuminen)) {
            osallistuminenText = osallistuminenTranslations.get(osallistuminen);
        } else {
            osallistuminenText = ElementUtil.createI18NAsIs(StringUtil.safeToString(osallistuminen));
        }
    }

    public String getPisteet() {
        return pisteet;
    }

    public void setPisteet(String pisteet) {
        this.pisteet = pisteet;
    }

}
