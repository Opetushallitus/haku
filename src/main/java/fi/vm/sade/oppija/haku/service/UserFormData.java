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
    private Map<String, Map<String, String>> formData = new HashMap<String, Map<String, String>>();

    public Map<String, String> getCategoryData(final String categoryId) {
        return formData.get(categoryId);
    }

    public void setValue(final String categoryId, final Map<String, String> values) {
        final HashMap<String, String> newValues = new HashMap<String, String>();
        newValues.putAll(values);
        this.formData.put(categoryId, newValues);
    }

}
