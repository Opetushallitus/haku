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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;

public class ComplexObjectIdDeserializer extends JsonDeserializer<ObjectId> {

    @Override
    public ObjectId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final ObjectCodec codec = jsonParser.getCodec();
        final JsonNode treeNode = codec.readTree(jsonParser);
        if (treeNode.has("time") && treeNode.has("machine") && treeNode.has("inc")) {
            return processSplinteredId(treeNode);
        }
        else if (treeNode.isTextual()) {
            final String idString = treeNode.getTextValue();
            if (idString.length() == 0 )
                return null;
            final ObjectId objectId = new ObjectId(idString);
            return objectId;
        }
        return null;
    }

    private final ObjectId processSplinteredId(final JsonNode treeNode) {
        final int time = treeNode.get("time").asInt();
        final int machine = treeNode.get("machine").asInt();
        final int inc = treeNode.get("inc").asInt();

        return ObjectId.createFromLegacyFormat(time, machine, inc);
    }
}
