package fi.vm.sade.oppija.haku.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("session")
public class Application {
    private String userId;
    private String applicationId;

    private Map<String, Map<String, String>> formData = new HashMap<String, Map<String, String>>();

    public Map<String, String> getCategoryData(final String categoryId) {
        return formData.get(categoryId);
    }

    public void setValue(final String categoryId, final Map<String, String> values) {
        final HashMap<String, String> newValues = new HashMap<String, String>();
        newValues.putAll(values);
        this.formData.put(categoryId, newValues);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
