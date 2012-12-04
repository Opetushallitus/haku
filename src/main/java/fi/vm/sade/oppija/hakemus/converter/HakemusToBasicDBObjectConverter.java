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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.hakemus.domain.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

/**
 * @author jukka
 * @version 11/22/124:39 PM}
 * @since 1.1
 */
public class HakemusToBasicDBObjectConverter implements Converter<Application, BasicDBObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HakemusToBasicDBObjectConverter.class);

    @Override
    public BasicDBObject convert(Application application) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);

        final Map m = mapper.convertValue(application, Map.class);
        final BasicDBObject basicDBObject = new BasicDBObject(m);
        LOGGER.debug(JSON.serialize(basicDBObject));
        return basicDBObject;
    }
}
