package fi.vm.sade.oppija.haku.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/11/122:47 PM}
 * @since 1.1
 */
@Component("session")
public class UserFormData {
    private Map<String, String> formData = new HashMap<String, String>();

    public Map<String, String> getFormData() {
        return formData;
    }

    public void setValue(final String key, final String value) {
        this.formData.put(key, value);
    }

    public void setValue(final Map<String, String> values) {
        this.formData.putAll(values);
    }
}
