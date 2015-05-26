package fi.vm.sade.haku.virkailija.valinta;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MapJsonAdapter implements JsonDeserializer<Map<String, String>>, JsonSerializer<Map<String, String>> {

    @Override
    public Map<String, String> deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }

        Map<String, String> map = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            map.put(key, handlePrimitive(entry.getValue()));
        }

        return map;

    }

    private String handlePrimitive(JsonElement elem) {
        if (elem.isJsonPrimitive()) {
            return elem.getAsString();
        }
        throw new IllegalArgumentException("Cannot be converted to Map<String, String>");
    }

    @Override
    public JsonElement serialize(Map<String, String> stringStringMap, Type type, JsonSerializationContext jsonSerializationContext) {
        return null;
    }
}
