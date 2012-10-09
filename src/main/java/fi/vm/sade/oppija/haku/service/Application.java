/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

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
