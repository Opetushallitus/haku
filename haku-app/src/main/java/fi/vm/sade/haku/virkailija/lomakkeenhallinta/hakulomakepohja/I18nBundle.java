package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static fi.vm.sade.haku.oppija.lomake.domain.I18nText.LANGS;

public class I18nBundle {

    private static Logger log = LoggerFactory.getLogger(ElementUtil.class);
    private final Map<String, I18nText> i18nBundle = new HashMap<String, I18nText>();

    public I18nBundle(final String bundleName) {
        ResourceBundle commonBundle = ResourceBundle.getBundle("form_common", new Locale("fi"));
        Set<String> propertyKeys = commonBundle.keySet();

        try {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, new Locale("fi"));
            propertyKeys.addAll(bundle.keySet());
        } catch (MissingResourceException mre) {
            log.warn("Bundle {} not found", bundleName);
        }

        for (String propertyKey : propertyKeys) {
            System.out.println("--------" + propertyKey);
        }

        for (String key : propertyKeys) {
            Map<String, String> translations = new HashMap<String, String>();
            String lowerCaseKey = key.toLowerCase();
            for (String lang : LANGS) {
                String text = getString(bundleName, lowerCaseKey, lang);
                if (text != null) {
                    translations.put(lang, text);
                }
            }
            i18nBundle.put(lowerCaseKey, new I18nText(translations));
        }
    }

    private static String getString(final String bundleName, final String key, final String lang) {
        String lowerCaseKey = key.toLowerCase();
        String text = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, new Locale(lang));
            if (bundle.containsKey(lowerCaseKey)) {
                text = bundle.getString(lowerCaseKey);
            } else {
                ResourceBundle commonBundle = ResourceBundle.getBundle("form_common", new Locale(lang));
                text = commonBundle.getString(lowerCaseKey);
            }
        } catch (MissingResourceException mre) {
        }
        return text;

    }

    public I18nText get(final String key) {
        String keyLowerCase = key.toLowerCase().replaceAll("-", ".");
        if (this.i18nBundle.containsKey(keyLowerCase)) {
            return this.i18nBundle.get(keyLowerCase);
        } else {
            //return null;
            return ElementUtil.createI18NAsIs("***" + keyLowerCase + "***");
        }
    }
}
