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
package fi.vm.sade.oppija.lomake.service.impl;

import fi.vm.sade.oppija.lomake.service.UserHolder;
import fi.vm.sade.oppija.lomake.service.UserPrefillDataService;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mikko Majapuro
 */
@Service
public class UserPrefillDataServiceImpl implements UserPrefillDataService {
    
    UserHolder userHolder;
     public static final Logger LOGGER = LoggerFactory.getLogger(UserPrefillDataServiceImpl.class);
    
    @Autowired
    public UserPrefillDataServiceImpl(UserHolder userHolder) {
        this.userHolder = userHolder;
    }

    @Override
    public Map<String, String> getUserPrefillData() {
        LOGGER.debug("getUserPrefillData");
        return userHolder.getUserPrefillData();
    }

    @Override
    public Map<String, String> populateWithPrefillData(Map<String, String> data) {
        LOGGER.debug("populateWithPrefillData {}", new Object[]{data});
        fetchUserPrefillData();
        Map<String, String> populated = new HashMap<String, String>(userHolder.getUserPrefillData());
        populated.putAll(data);
        LOGGER.debug("prefill data populated {}", new Object[]{populated});
        return populated;
    }
    
    @Override
    public void addUserPrefillData(Map<String, String> data) {
        LOGGER.debug("addUserPrefillData {}", new Object[]{data});
        userHolder.getUserPrefillData().putAll(data);
    }
    
    private void fetchUserPrefillData() {
        if (userHolder.isUserKnown()) {
            //TODO: implement this and remove mock implementations
            userHolder.getUserPrefillData().put("Sähköposti", "esitaytetty_email@autofill.com");
        }
    }
}
