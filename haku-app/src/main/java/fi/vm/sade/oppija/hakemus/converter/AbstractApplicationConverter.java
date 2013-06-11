package fi.vm.sade.oppija.hakemus.converter;

import com.google.common.base.Strings;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.service.hakemus.schema.HakukohdeTyyppi;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */
public abstract class AbstractApplicationConverter<T> implements Converter<Application, T> {

    protected List<HakukohdeTyyppi> getPreferences(Map<String, String> keyValues) {
        List<HakukohdeTyyppi> preferences = new ArrayList<HakukohdeTyyppi>();
        int i = 1;

        while(true) {
            String key = String.format(OppijaConstants.PREFERENCE_ID, i);
            if (keyValues.containsKey(key)) {
                String value = keyValues.get(key);
                if (value != null && !value.isEmpty()) {
                    HakukohdeTyyppi preference = new HakukohdeTyyppi();
                    preference.setHakukohdeOid(value);
                    preference.setPrioriteetti(i);
                    preference.setHarkinnanvaraisuus(isDiscretionary(keyValues, i));
                    preferences.add(preference);
                    ++i;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return preferences;
    }

    protected String getFirstNames(Map<String, String> keyValues) {
        return keyValues.get(OppijaConstants.ELEMENT_ID_FIRST_NAMES);
    }

    protected String getLastName(Map<String, String> keyValues) {
        return keyValues.get(OppijaConstants.ELEMENT_ID_LAST_NAME);
    }

    protected boolean isDiscretionary(Map<String, String> keyValues, int index) {
        String key = String.format(OppijaConstants.PREFERENCE_DISCRETIONARY, index);
        if (keyValues.containsKey(key)) {
            String value = keyValues.get(key);
            if (!Strings.isNullOrEmpty(value)) {
                return Boolean.parseBoolean(value);
            }
        }
        return false;
    }
}
