package fi.vm.sade.oppija.ui.selenium;

import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.phase.koulutustausta.KoulutustaustaPhase;

import java.util.LinkedHashMap;

public final class DefaultValues {

    public static final String OPETUSPISTE = "FAKTIA, Espoo op";

    public final LinkedHashMap<String, String> henkilotiedot = new LinkedHashMap<String, String>();
    public final LinkedHashMap<String, String> koulutustausta_pk = new LinkedHashMap<String, String>();
    public final LinkedHashMap<String, String> koulutustausta_lk = new LinkedHashMap<String, String>();
    public final LinkedHashMap<String, String> lisatiedot = new LinkedHashMap<String, String>();

    public DefaultValues() {
        henkilotiedot.put("Sukunimi", "Ankka");
        henkilotiedot.put("Etunimet", "Aku Kalle");
        henkilotiedot.put("Kutsumanimi", "AKu");
        henkilotiedot.put("Henkilotunnus", "010113-668B");
        henkilotiedot.put("Sähköposti", "aku.ankka@ankkalinna.al");
        henkilotiedot.put("matkapuhelinnumero1", "0501000100");
        henkilotiedot.put("aidinkieli", "FI");
        henkilotiedot.put("asuinmaa", "FIN");
        henkilotiedot.put("kotikunta", "jalasjarvi");
        henkilotiedot.put("lahiosoite", "Katu 1");
        henkilotiedot.put("Postinumero", "00100");

        koulutustausta_lk.put("POHJAKOULUTUS_" + KoulutustaustaPhase.TUTKINTO_YLIOPPILAS, null);
        koulutustausta_lk.put("lukioPaattotodistusVuosi", "2012");
        koulutustausta_lk.put("ammatillinenTutkintoSuoritettu_false", null);
        koulutustausta_lk.put("lukion_kieli", "FI");

        koulutustausta_pk.put("POHJAKOULUTUS_tutkinto1", null);
        koulutustausta_pk.put("PK_PAATTOTODISTUSVUOSI", "2012");
        koulutustausta_pk.put("KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON_false", null);
        koulutustausta_pk.put("perusopetuksen_kieli", "FI");

        lisatiedot.put("TYOKOKEMUSKUUKAUDET", "10");
        lisatiedot.put("asiointikieli_suomi", null);
        lisatiedot.put("lupaMarkkinointi", null);
        lisatiedot.put("lupaJulkaisu", null);
    }


}
