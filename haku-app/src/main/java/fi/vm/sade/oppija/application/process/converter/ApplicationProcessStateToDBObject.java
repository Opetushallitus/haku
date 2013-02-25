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
package fi.vm.sade.oppija.application.process.converter;

import com.google.common.base.Function;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessState;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.util.Map;

/**
 * @author Mikko Majapuro
 */
public class ApplicationProcessStateToDBObject implements Function<ApplicationProcessState, DBObject> {

    @Override
    public DBObject apply(final ApplicationProcessState applicationProcessState) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        Map map = mapper.convertValue(applicationProcessState, Map.class);
        return new BasicDBObject(map);
    }
}
