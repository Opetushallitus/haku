package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.valinta.dto.Osallistuminen;
import fi.vm.sade.haku.virkailija.valinta.dto.PistetietoDTO;

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
    private static final Map<String, String> nameOverriddenDisplayValues = new HashMap<>();

    static {
        osallistuminenTranslations = new HashMap<Osallistuminen, I18nText>(7);
        osallistuminenTranslations.put(Osallistuminen.OSALLISTUU,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.osallistuminen.osallistuu", OppijaConstants.MESSAGES_BUNDLE_NAME));
        osallistuminenTranslations.put(Osallistuminen.EI_OSALLISTU,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.osallistuminen.eiosallistu", OppijaConstants.MESSAGES_BUNDLE_NAME));
        osallistuminenTranslations.put(Osallistuminen.VIRHE,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.osallistuminen.virhe", OppijaConstants.MESSAGES_BUNDLE_NAME));
        osallistuminenTranslations.put(Osallistuminen.MERKITSEMATTA,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.osallistuminen.merkitsematta", OppijaConstants.MESSAGES_BUNDLE_NAME));
        osallistuminenTranslations.put(Osallistuminen.OSALLISTUI,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.osallistuminen.osallistui", OppijaConstants.MESSAGES_BUNDLE_NAME));
        osallistuminenTranslations.put(Osallistuminen.EI_OSALLISTUNUT,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.osallistuminen.eiosallistunut", OppijaConstants.MESSAGES_BUNDLE_NAME));
        osallistuminenTranslations.put(Osallistuminen.EI_VAADITA,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.osallistuminen.eivaadita", OppijaConstants.MESSAGES_BUNDLE_NAME));

        pisteetOverriddenDisplayValues.put("true", "Hyväksytty");
        pisteetOverriddenDisplayValues.put("false", "Hylätty");

        nameOverriddenDisplayValues.put("kielikoe_fi", "Ammatillisen koulutuksen valtakunnallinen kielikoe");
        nameOverriddenDisplayValues.put("kielikoe_sv", "Ammatillisen koulutuksen valtakunnallinen kielikoe");
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
        if (nameOverriddenDisplayValues.containsKey(id)) {
            this.nimi = ElementUtil.createI18NAsIs(nameOverriddenDisplayValues.get(id));
        } else {
            this.nimi = nimi;
        }
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
