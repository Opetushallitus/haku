package fi.vm.sade.oppija.haku.service;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Application {
    private String userId;
    private String applicationId;

    public Map<String, Map<String, String>> getApplicationData() {
        return applicationData;
    }

    public void setApplicationData(Map<String, Map<String, String>> applicationData) {
        this.applicationData = applicationData;
    }

    private Map<String, Map<String, String>> applicationData;

    public Application() {
        this.applicationData = new HashMap<String, Map<String, String>>();
    }

    public Application(String userId, String applicationId) {
        this.userId = userId;
        this.applicationId = applicationId;
        this.applicationData = new HashMap<String, Map<String, String>>();
    }

    public Map<String, String> getCategoryData(final String categoryId) {
        return applicationData.get(categoryId);
    }

    public void setValue(final String categoryId, final Map<String, String> values) {
        final HashMap<String, String> newValues = new HashMap<String, String>();
        newValues.putAll(values);
        this.applicationData.put(categoryId, newValues);
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
