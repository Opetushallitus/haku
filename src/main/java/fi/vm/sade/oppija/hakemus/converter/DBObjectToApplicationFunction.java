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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.hakemus.converter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Function;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.oppija.lomake.service.EncrypterService;
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

    private final EncrypterService encrypterService;

    @Autowired
    public DBObjectToApplicationFunction(@Qualifier("aesEncrypter") EncrypterService encrypterService) {
        this.encrypterService = encrypterService;
    }

    @Override
    public Application apply(DBObject dbObject) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);

        final Map fromValue = dbObject.toMap();
        final Map<String, Map<String, String>> vastaukset = (Map<String, Map<String, String>>) fromValue.get("vastaukset");
        if (vastaukset != null) {
            final Map<String, String> henkilotiedot = vastaukset.get("henkilotiedot");
            if (henkilotiedot != null && henkilotiedot.containsKey(SocialSecurityNumber.HENKILOTUNNUS)) {
                final String hetu = henkilotiedot.get(SocialSecurityNumber.HENKILOTUNNUS);
                henkilotiedot.put(SocialSecurityNumber.HENKILOTUNNUS, encrypterService.decrypt(hetu));
            }
        }
        return mapper.convertValue(fromValue, Application.class);
    }
}
