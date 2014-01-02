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
package fi.vm.sade.haku.oppija.hakemus.converter;

import com.google.common.base.Function;
import com.mongodb.DBObject;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.haku.oppija.lomake.service.EncrypterService;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author jukka
 * @version 11/22/125:11 PM}
 * @since 1.1
 */

@Service
public class DBObjectToApplicationFunction implements Function<DBObject, Application> {

    private static final Logger LOG = LoggerFactory.getLogger(DBObjectToApplicationFunction.class);

    private final EncrypterService encrypterService;

    @Autowired
    public DBObjectToApplicationFunction(@Qualifier("aesEncrypter") EncrypterService encrypterService) {
        this.encrypterService = encrypterService;
    }

    @Override
    public Application apply(DBObject dbObject) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        mapper.enable(DeserializationConfig.Feature.READ_ENUMS_USING_TO_STRING);
        mapper.enable(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING);

        @SuppressWarnings("rawtypes")
        final Map fromValue = dbObject.toMap();
        @SuppressWarnings("unchecked")
        final Map<String, Map<String, String>> answers = (Map<String, Map<String, String>>) fromValue.get("answers");
        if (answers != null) {
            final Map<String, String> henkilotiedot = answers.get("henkilotiedot");
            if (henkilotiedot != null && henkilotiedot.containsKey(SocialSecurityNumber.HENKILOTUNNUS)) {
                final String hetu = henkilotiedot.get(SocialSecurityNumber.HENKILOTUNNUS);
                henkilotiedot.put(SocialSecurityNumber.HENKILOTUNNUS, encrypterService.decrypt(hetu));
            }
        }
        Application app = null;
        try {
            app = mapper.convertValue(fromValue, Application.class);
        } catch (IllegalArgumentException iae) {
            LOG.info("Could not convert DBObject to Application : " + fromValue);
            throw iae;
        }
        return app;
    }
}
