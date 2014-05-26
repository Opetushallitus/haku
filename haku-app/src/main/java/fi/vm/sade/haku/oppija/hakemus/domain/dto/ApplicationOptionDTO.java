package fi.vm.sade.haku.oppija.hakemus.domain.dto;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.sijoittelu.tulos.dto.HakemuksenTila;
import fi.vm.sade.sijoittelu.tulos.dto.ValintatuloksenTila;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ApplicationOptionDTO {

    private String oid;
    private int index;
    private String name;
    private String opetuspiste;
    private String opetuspisteOid;
    private List<Pistetieto> pistetiedot = new ArrayList<Pistetieto>();

    private String yhteispisteet;
    private HakemuksenTila sijoittelunTulos;
    private I18nText sijoittelunTulosText;

    private ValintatuloksenTila vastaanottoTieto;
    private I18nText vastaanottoTietoText;

    private String jonoId;

    private static final String BUNDLE_NAME = "messages";
    private static final Map<HakemuksenTila, I18nText> sijoittelunTulosTranslations;
    private static final Map<ValintatuloksenTila, I18nText> vastaanottoTietoTranslations;

    static {
        sijoittelunTulosTranslations = new HashMap<HakemuksenTila, I18nText>(6);
        sijoittelunTulosTranslations.put(HakemuksenTila.HYLATTY,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.sijoittelu.hylatty", BUNDLE_NAME));
        sijoittelunTulosTranslations.put(HakemuksenTila.HYVAKSYTTY,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.sijoittelu.hyvaksytty", BUNDLE_NAME));
        sijoittelunTulosTranslations.put(HakemuksenTila.PERUNUT,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.sijoittelu.perunut", BUNDLE_NAME));
        sijoittelunTulosTranslations.put(HakemuksenTila.PERUUNTUNUT,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.sijoittelu.peruuntunut", BUNDLE_NAME));
        sijoittelunTulosTranslations.put(HakemuksenTila.PERUUTETTU,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.sijoittelu.peruutettu", BUNDLE_NAME));
        sijoittelunTulosTranslations.put(HakemuksenTila.VARALLA,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.sijoittelu.varalla", BUNDLE_NAME));

        vastaanottoTietoTranslations = new HashMap<ValintatuloksenTila, I18nText>();
        vastaanottoTietoTranslations.put(ValintatuloksenTila.EI_VASTAANOTETTU_MAARA_AIKANA,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.vastaanotto.eiMaaraaikana", BUNDLE_NAME));
        vastaanottoTietoTranslations.put(ValintatuloksenTila.ILMOITETTU,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.vastaanotto.ilmoitettu", BUNDLE_NAME));
        vastaanottoTietoTranslations.put(ValintatuloksenTila.PERUNUT,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.vastaanotto.perunut", BUNDLE_NAME));
        vastaanottoTietoTranslations.put(ValintatuloksenTila.PERUUTETTU,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.vastaanotto.peruutettu", BUNDLE_NAME));
        vastaanottoTietoTranslations.put(ValintatuloksenTila.VASTAANOTTANUT_LASNA,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.vastaanotto.lasna", BUNDLE_NAME));
        vastaanottoTietoTranslations.put(ValintatuloksenTila.VASTAANOTTANUT_POISSAOLEVA,
                ElementUtil.createI18NText("virkailija.hakemus.valintatiedot.vastaanotto.poissa", BUNDLE_NAME));
    }


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

    public I18nText getSijoittelunTulosText() {
        return sijoittelunTulosText;
    }

    public HakemuksenTila getSijoittelunTulos() {
        return sijoittelunTulos;
    }

    public void setSijoittelunTulos(HakemuksenTila sijoittelunTulos) {
        this.sijoittelunTulos = sijoittelunTulos;
        if (sijoittelunTulosTranslations.containsKey(sijoittelunTulos)) {
            sijoittelunTulosText = sijoittelunTulosTranslations.get(sijoittelunTulos);
        } else {
            sijoittelunTulosText = ElementUtil.createI18NAsIs(StringUtil.safeToString(sijoittelunTulos));
        }
    }

    public ValintatuloksenTila getVastaanottoTieto() {
        return vastaanottoTieto;
    }

    public I18nText getVastaanottoTietoText() {
        return vastaanottoTietoText;
    }

    public void setVastaanottoTieto(ValintatuloksenTila vastaanottoTieto) {
        this.vastaanottoTieto = vastaanottoTieto;
        if (vastaanottoTietoTranslations.containsKey(vastaanottoTieto)) {
            vastaanottoTietoText = vastaanottoTietoTranslations.get(vastaanottoTieto);
        } else {
            vastaanottoTietoText = ElementUtil.createI18NAsIs(StringUtil.safeToString(vastaanottoTieto));
        }
    }

    public List<Pistetieto> getPistetiedot() {
        return pistetiedot;
    }

    public void setPistetiedot(List<Pistetieto> pistetiedot) {
        this.pistetiedot = pistetiedot;
    }

    public void addPistetieto(Pistetieto pistetieto) {
        pistetiedot.add(pistetieto);
    }

    public String getYhteispisteet() {
        return yhteispisteet;
    }

    public void setYhteispisteet(String yhteispisteet) {
        this.yhteispisteet = yhteispisteet;
    }

    public void sortPistetiedot() {
        Collections.sort(pistetiedot, new Comparator<Pistetieto>() {
            @Override
            public int compare(Pistetieto thiz, Pistetieto other) {
                if (thiz.getOsallistuminen() != null && other.getOsallistuminen() == null) {
                    return -1;
                } else if (thiz.getOsallistuminen() == null && other.getOsallistuminen() != null) {
                    return 1;
                }
                if (isNotBlank(thiz.getPisteet()) && isBlank(other.getPisteet())) {
                    return -1;
                } else if (isBlank(thiz.getPisteet()) && isNotBlank(other.getPisteet())) {
                    return 1;
                }
                if (thiz.getId() == null) {
                    return 1;
                }
                if (other.getId() == null) {
                    return -1;
                }
                return (thiz.getId().compareTo(other.getId()));
            }
        });
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getJonoId() {
        return jonoId;
    }

    public void setJonoId(String jonoId) {
        this.jonoId = jonoId;
    }
}
