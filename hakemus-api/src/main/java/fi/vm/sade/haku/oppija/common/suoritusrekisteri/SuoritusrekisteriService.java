package fi.vm.sade.haku.oppija.common.suoritusrekisteri;

import java.util.List;
import java.util.Map;

public interface SuoritusrekisteriService {

    public static String PERUSOPETUS_KOMO = "1.2.246.562.13.62959769647";

    // Lisäopetus, tuttavallisemmin kymppiluokka
    public static String LISAOPETUS_KOMO = "1.2.246.562.5.2013112814572435044876";

    public static String ULKOMAINEN_KOMO ="1.2.246.562.13.86722481404";
    public static String AMMATTISTARTTI_KOMO = "1.2.246.562.5.2013112814572438136372";

    public static String MAMU_VALMENTAVA_KOMO = "1.2.246.562.5.2013112814572441001730";

    public static String LUKIOON_VALMISTAVA_KOMO = "1.2.246.562.5.2013112814572429142840";

    // Sure tuntee kuntouttavan valmentavana
    public static String KUNTOUTTAVA_KOMO = "1.2.246.562.5.2013112814572435755085";

    // Lukion komoOid on aikuisten oikeasti "TODO lukio komo oid", ainakin toistaiseksi
    public static String LUKIO_KOMO = "TODO lukio komo oid";

    public static String YO_TUTKINTO_KOMO = "1.2.246.562.5.2013061010184237348007";


    /**
     * Palauttaa henkilön suoritukset mappina komoOid - suoritus.
     * @param personOid
     * @return suoritukset mappina komoOid - suoritus
     */
    Map<String, List<SuoritusDTO>> getSuoritukset(String personOid);

    List<OpiskelijaDTO> getOpiskelijatiedot(String personOid);

    List<ArvosanaDTO> getArvosanat(String suoritusId);

    Map<String, List<SuoritusDTO>> getSuoritukset(String personOid, String komoOid);
}
