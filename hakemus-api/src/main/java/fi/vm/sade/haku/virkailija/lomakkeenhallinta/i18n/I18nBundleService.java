package fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n;

import com.google.common.base.Joiner;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.I18nBundle;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class I18nBundleService {

    /* file naming mappings BEGIN */
    private static final Map<String, String> HAKUTAVAT = new HashMap<String, String>() {{
        put(OppijaConstants.HAKUTAPA_YHTEISHAKU, "yhteishaku");
        put(OppijaConstants.HAKUTAPA_ERILLISHAKU, "erillishaku");
        put(OppijaConstants.HAKUTAPA_JATKUVA_HAKU, "jatkuva");
    }};

    private static final Map<String, String> HAKUTYYPIT = new HashMap<String, String>() {{
        put(OppijaConstants.HAKUTYYPPI_VARSINAINEN_HAKU, "varsinainen");
        put(OppijaConstants.HAKUTYYPPI_TAYDENNYS, "taydennys");
        put(OppijaConstants.HAKUTYYPPI_LISAHAKU, "lisahaku");
    }};

    private static final Map<String, String> KOHDEJOUKOT = new HashMap<String, String>() {{
        put(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU, "korkeakoulu");
        put(OppijaConstants.KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA, "pervako");
        put(OppijaConstants.KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN, "ammatillinenerityis");
    }};

    private static final Map<String, String> HAKUKAUDET = new HashMap<String, String>() {{
        put(OppijaConstants.HAKUKAUSI_KEVAT, "kevat");
        put(OppijaConstants.HAKUKAUSI_SYKSY, "syksy");
    }};

    private static final Map<String, String> KOHDEJOUKON_TARKENTEET = new HashMap<String, String>() {{
        put(OppijaConstants.KOHDEJOUKON_TARKENNE_AMK_OPE, "amk_ope");
        put(OppijaConstants.KOHDEJOUKON_TARKENNE_AMK_OPO, "amk_opo");
        put(OppijaConstants.KOHDEJOUKON_TARKENNE_AMK_ERKKA, "amk_erkka");
    }};

    /* file naming mappings END */

    private static final String FILE_NAME_PREFIX = "form_messages";

    final Map<String, SoftReference<I18nBundle>> applicationSystemTranslations = new ConcurrentHashMap<String, SoftReference<I18nBundle>>();
    final ApplicationSystemService applicationSystemService;

    @Autowired
    public I18nBundleService(ApplicationSystemService applicationSystemService) {
        this.applicationSystemService = applicationSystemService;
    }

    public I18nBundle getBundle(String applicationSystemOid) {
        ApplicationSystem applicationSystem = applicationSystemService.getApplicationSystem(applicationSystemOid);
        return getBundle(applicationSystem);
    }

    public I18nBundle getBundle(ApplicationSystem applicationSystem) {
        I18nBundle i18nBundle = getCachedBundle(applicationSystem.getId());
        if (null != i18nBundle)
            return i18nBundle;
        return initializeandReturnBundle(applicationSystem);
    }

    private I18nBundle getCachedBundle(String applicationSystemOid) {
        if (applicationSystemTranslations.containsKey(applicationSystemOid)) {
            return applicationSystemTranslations.get(applicationSystemOid).get();
        }
        return null;
    }

    private I18nBundle initializeandReturnBundle(final ApplicationSystem applicationSystem) {
        final I18nBundle i18nBundle = new I18nBundle(getMessageBundleName(FILE_NAME_PREFIX, applicationSystem),
                getMessageBundleNameWithTarkenne(FILE_NAME_PREFIX, applicationSystem),
                (FILE_NAME_PREFIX + "_" + applicationSystem.getId().replace('.', '_')));
        this.applicationSystemTranslations.put(applicationSystem.getId(), new SoftReference<>(i18nBundle));
        return i18nBundle;
    }

    private static String getMessageBundleName(final String baseName, final ApplicationSystem as) {
        return Joiner.on('_').join(baseName,
                HAKUTAVAT.get(as.getHakutapa()),
                HAKUTYYPIT.get(as.getApplicationSystemType()),
                HAKUKAUDET.get(as.getHakukausiUri()),
                KOHDEJOUKOT.containsKey(as.getKohdejoukkoUri()) ? KOHDEJOUKOT.get(as.getKohdejoukkoUri()) : "muu");
    }

    private static String getMessageBundleNameWithTarkenne(final String baseName, final ApplicationSystem as) {
        if(as.getKohdejoukonTarkenne() == null || as.getKohdejoukonTarkenne().trim().equals("")) {
            return null;
        } else {
            return Joiner.on('_').join(baseName,
                    HAKUTAVAT.get(as.getHakutapa()),
                    HAKUTYYPIT.get(as.getApplicationSystemType()),
                    HAKUKAUDET.get(as.getHakukausiUri()),
                    KOHDEJOUKOT.containsKey(as.getKohdejoukkoUri()) ? KOHDEJOUKOT.get(as.getKohdejoukkoUri()) : "muu",
                    KOHDEJOUKON_TARKENTEET.containsKey(as.getKohdejoukonTarkenne()) ? KOHDEJOUKON_TARKENTEET.get(as.getKohdejoukonTarkenne()) : "muu");
        }
    }
}
