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
package fi.vm.sade.oppija.hakemus.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.oppija.lomake.service.EncrypterService;

/**
 * @author jukka
 * @version 11/22/124:39 PM}
 * @since 1.1
 */
@Service
public class ApplicationToDBObjectFunction implements Function<Application, DBObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationToDBObjectFunction.class);
    private final EncrypterService aesEncypter;
    private final EncrypterService shaEncrypter;

    @Autowired
    public ApplicationToDBObjectFunction(@Qualifier("aesEncrypter") EncrypterService aesEncypter, @Qualifier("shaEncrypter") EncrypterService shaEncrypter) {
        this.aesEncypter = aesEncypter;
        this.shaEncrypter = shaEncrypter;
    }

    @Override
    public DBObject apply(Application application) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        mapper.disable(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS);
        mapper.disable(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES);

        @SuppressWarnings("rawtypes")
        final Map m = mapper.convertValue(application, Map.class);
        @SuppressWarnings("unchecked")
        final Map<String, Map<String, String>> answers = (Map<String, Map<String, String>>) m.get("answers");

        if (answers != null) {
            final Map<String, String> henkilotiedot = answers.get("henkilotiedot");
            if (henkilotiedot != null && henkilotiedot.containsKey(SocialSecurityNumber.HENKILOTUNNUS)) {
                final String hetu = henkilotiedot.get(SocialSecurityNumber.HENKILOTUNNUS);
                henkilotiedot.put("syntymaaika", ssnToDateOfBirth(hetu));
                henkilotiedot.put(SocialSecurityNumber.HENKILOTUNNUS, aesEncypter.encrypt(hetu));
                henkilotiedot.put(SocialSecurityNumber.HENKILOTUNNUS_HASH, shaEncrypter.encrypt(hetu));
            }
        }
        final BasicDBObject basicDBObject = new BasicDBObject(m);
        LOGGER.debug(JSON.serialize(basicDBObject));
        return basicDBObject;
    }
    
    private String ssnToDateOfBirth(String ssn) {
        HashMap<String, Integer> centuries = new HashMap<String, Integer>();
        centuries.put("+", 1800); // NOSONAR
        centuries.put("-", 1900); // NOSONAR
        centuries.put("a", 2000); // NOSONAR
        centuries.put("A", 2000); // NOSONAR
        DateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd");
        isoDate.setLenient(false);

        String day = ssn.substring(0, 2); // NOSONAR
        String month = ssn.substring(2, 4); // NOSONAR
        String year = Integer.toString((centuries.get(ssn.substring(6, 7)) + // NOSONAR 
                Integer.valueOf(ssn.substring(4, 6)))); // NOSONAR
        String dob = String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day);
        try {
            isoDate.parse(dob);
            return dob;
        } catch (ParseException pe) {
            // Definitely shouldn't happen, SSN should've been checked before getting into db.
            // Letting it slide now, but I'll fix this. Later. Promise.
            return null;
        }
    }
}
