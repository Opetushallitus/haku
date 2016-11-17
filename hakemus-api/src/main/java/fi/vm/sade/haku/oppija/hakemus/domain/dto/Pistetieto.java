package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.valinta.dto.Osallistuminen;
import fi.vm.sade.haku.virkailija.valinta.dto.PistetietoDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Pistetieto {

    private String id;
    private I18nText nimi;
    private Osallistuminen osallistuminen;
    private I18nText osallistuminenText;
    private String pisteet;
    private String pisteetToDisplay;
    
    private static final Map<Osallistuminen, I18nText> osallistuminenTranslations;
    private static final Map<String, String> pisteetOverriddenDisplayValues = new HashMap<>();

    static {
        osallistuminenTranslations = new HashMap<Osallistuminen, I18nText>(3);
        osallistuminenTranslations.put(Osallistuminen.OSALLISTUU,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.osallistuminen.osallistuu", OppijaConstants.MESSAGES_BUNDLE_NAME));
        osallistuminenTranslations.put(Osallistuminen.EI_OSALLISTU,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.osallistuminen.eiOsallistu", OppijaConstants.MESSAGES_BUNDLE_NAME));
        osallistuminenTranslations.put(Osallistuminen.VIRHE,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.osallistuminen.virhe", OppijaConstants.MESSAGES_BUNDLE_NAME));

        pisteetOverriddenDisplayValues.put("hyvaksytty", "Hyväksytty");
        pisteetOverriddenDisplayValues.put("hylatty", "Hylätty");
        pisteetOverriddenDisplayValues.put("ei_osallistunut", "Ei osallistunut");
    }

    public Pistetieto() {

    }

    public Pistetieto(PistetietoDTO pistetietoDTO) {
        this.id = pistetietoDTO.getTunniste();
        this.pisteet = pistetietoDTO.getLaskennallinenArvo();
        this.osallistuminenText = ElementUtil.createI18NAsIs(pistetietoDTO.getOsallistuminen());
        this.pisteetToDisplay = renderPisteet(this.pisteet);
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
        this.pisteetToDisplay = renderPisteet(this.pisteet);
    }

    private static String renderPisteet(String pisteetValue) {
        if (pisteetOverriddenDisplayValues.containsKey(pisteetValue)) {
            return pisteetOverriddenDisplayValues.get(pisteetValue);
        }
        return pisteetValue;
    }

    public String getPisteetToDisplay() {
        return pisteetToDisplay;
    }
}
