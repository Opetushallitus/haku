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
