package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.TranslationsUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static fi.vm.sade.haku.oppija.lomake.domain.I18nText.LANGS;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.FORM_COMMON_BUNDLE_NAME;

public class I18nBundle {

    private static Logger log = LoggerFactory.getLogger(ElementUtil.class);
    private final Map<String, I18nText> i18nBundle = new HashMap<String, I18nText>();

    public I18nBundle(final String... bundleNames) {
        final List<String> bundleNamesList = new ArrayList<String>(bundleNames.length + 1);

        bundleNamesList.add(FORM_COMMON_BUNDLE_NAME);
        log.debug("Creating message bundle...");
        for (String bundleName : bundleNames) {
            log.debug("Adding file to bundle: " + bundleName);
            if(bundleName != null)
                bundleNamesList.add(bundleName);
        }

        initializeBundle(bundleNamesList);
    }

    private void initializeBundle(final List<String> bundleNames){
        Set<String> propertyKeys = getPropertyKeys(bundleNames);

        for (String key : propertyKeys) {
            Map<String, String> translations = new HashMap<String, String>();
            String lowerCaseKey = key.toLowerCase();
            for (String bundleName : bundleNames) {
                for (String lang : LANGS) {
                    String text = getString(bundleName, lowerCaseKey, lang);
                    if (text != null) {
                        translations.put(lang, text);
                    }
                }
            }
            i18nBundle.put(lowerCaseKey, new I18nText(translations));
        }
    }

    private static String getString(final String bundleName, final String key, final String lang) {
        final String lowerCaseKey = key.toLowerCase();
        String text = null;
        try {
            final ResourceBundle bundle = ResourceBundle.getBundle(bundleName, new Locale(lang));
            if (bundle.containsKey(lowerCaseKey)) {
                text = bundle.getString(lowerCaseKey);
            }
        } catch (MissingResourceException mre) {
            //TODO: =RS= Change to load only once to stop flooding
            log.warn("Bundle {} not found when loading translations for {}", bundleName, lang);
        }
        return text;
    }

    private Set<String> getPropertyKeys(final List<String> bundleNames){
        final Set<String> propertyKeys = new HashSet<String>();
        for (String bundleName : bundleNames) {
            try {
                final ResourceBundle bundle = ResourceBundle.getBundle(bundleName, new Locale("fi"));
                propertyKeys.addAll(bundle.keySet());
            } catch (MissingResourceException mre) {
                log.warn("Bundle {} not found when loading properties", bundleName);
            }
        }
        return propertyKeys;
    }

    public I18nText get(final String key) {
        final String keyLowerCase = key.toLowerCase().replaceAll("-", ".");

        if (this.i18nBundle.containsKey(keyLowerCase)) {
            return TranslationsUtil.ensureDefaultLanguageTranslations(this.i18nBundle.get(keyLowerCase));
        } else {
            return null;
        }
    }
}
