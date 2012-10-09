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

package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.Date;

/**
 * @author jukka
 * @version 9/11/1210:15 AM}
 * @since 1.1
 */
public class ObjectIdDeserializer extends JsonDeserializer<org.bson.types.ObjectId> {

    @Override
    public ObjectId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        final ObjectCodec codec = jsonParser.getCodec();
        final JsonNode treeNode = codec.readTree(jsonParser);
        final long time = treeNode.get("time").longValue();
        final int machine = treeNode.get("machine").intValue();
        final int inc = treeNode.get("inc").intValue();

        final ObjectId objectId = new ObjectId(new Date(time), machine, inc);
        objectId.notNew();
        return objectId;
    }
}
