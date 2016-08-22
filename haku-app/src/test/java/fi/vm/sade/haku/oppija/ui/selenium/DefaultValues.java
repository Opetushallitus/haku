package fi.vm.sade.haku.oppija.ui.selenium;

import com.google.common.collect.Maps;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.LinkedHashMap;
import java.util.Map;

public final class DefaultValues {

    public static final String KYSYMYS_POHJAKOULUTUS = "POHJAKOULUTUS";
    public static final String TUTKINTO_ULKOMAINEN_TUTKINTO = "0";
    public static final String TUTKINTO_PERUSKOULU = "1";
    public static final String TUTKINTO_OSITTAIN_YKSILOLLISTETTY = "2";
    public static final String TUTKINTO_ERITYISOPETUKSEN_YKSILOLLISTETTY = "3";
    public static final String TUTKINTO_YKSILOLLISTETTY = "6";
    public static final String TUTKINTO_KESKEYTYNYT = "7";
    public static final String TUTKINTO_YLIOPPILAS = "9";

    public static final String OPETUSPISTE = "FAKTIA, Espoo op";

    public final Map<String, String> henkilotiedot = new LinkedHashMap<String, String>();
    public final Map<String, String> kkHenkilotiedot = new LinkedHashMap<String, String>();
    public final Map<String, String> koulutustausta_pk = new LinkedHashMap<String, String>();
    public final Map<String, String> koulutustausta_lk = new LinkedHashMap<String, String>();
    public final Map<String, String> lisatiedot = new LinkedHashMap<String, String>();
    public final Map<String, String> preference1 = new LinkedHashMap<String, String>();

    public DefaultValues() {
        henkilotiedot.put("Sukunimi", "Ankka");
        henkilotiedot.put("Etunimet", "Aku Kalle");
        henkilotiedot.put("Kutsumanimi", "AKu");
        henkilotiedot.put("Henkilotunnus", "010100A939R");
        henkilotiedot.put("onkosinullakaksoiskansallisuus", "false");
        henkilotiedot.put("Sähköposti", "aku.ankka@ankkalinna.al");
        henkilotiedot.put("matkapuhelinnumero1", "0501000100");
        henkilotiedot.put("aidinkieli", "FI");
        henkilotiedot.put("asuinmaa", "FIN");
        henkilotiedot.put("kotikunta", "jalasjarvi");
        henkilotiedot.put("lahiosoite", "Katu 1");
        henkilotiedot.put("Postinumero", "00100");

        kkHenkilotiedot.putAll(henkilotiedot);
        kkHenkilotiedot.put("koulusivistyskieli", "FI");

        koulutustausta_lk.put("POHJAKOULUTUS", "9");
        koulutustausta_lk.put(OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI, "2012");
        koulutustausta_lk.put("ammatillinenTutkintoSuoritettu", "false");
        koulutustausta_lk.put(OppijaConstants.LUKIO_KIELI, "FI");
        koulutustausta_lk.put(OppijaConstants.YLIOPPILASTUTKINTO, OppijaConstants.YLIOPPILASTUTKINTO_FI);

        koulutustausta_pk.put("POHJAKOULUTUS", "1");
        koulutustausta_pk.put(OppijaConstants.PERUSOPETUS_PAATTOTODISTUSVUOSI, "2012");
        koulutustausta_pk.put("KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON", "false");
        koulutustausta_pk.put(OppijaConstants.ELEMENT_ID_LISAKOULUTUS_KYMPPI, "true");
        koulutustausta_pk.put(OppijaConstants.KYMPPI_PAATTOTODISTUSVUOSI, "2012");
        koulutustausta_pk.put(OppijaConstants.PERUSOPETUS_KIELI, "FI");

        lisatiedot.put("TYOKOKEMUSKUUKAUDET", "10");
        lisatiedot.put("asiointikieli", "suomi");
        lisatiedot.put("lupaMarkkinointi", "true");
        lisatiedot.put("lupaJulkaisu", "true");

        preference1.put("preference1-discretionary", "false");
        preference1.put("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys", "true");
        preference1.put("preference1_sora_terveys", "false");
        preference1.put("preference1_sora_oikeudenMenetys", "false");
    }

    public Map<String, String> getHenkilotiedot(final Map<String, String> values) {
        return getNewMap(henkilotiedot, values);
    }

    public Map<String, String> getPreference1(final Map<String, String> values) {
        return getNewMap(preference1, values);
    }

    private Map<String, String> getNewMap(final Map<String, String> defaultValues, final Map<String, String> values) {
        LinkedHashMap<String, String> newMap = Maps.newLinkedHashMap(defaultValues);
        newMap.putAll(values);
        return newMap;
    }
}
