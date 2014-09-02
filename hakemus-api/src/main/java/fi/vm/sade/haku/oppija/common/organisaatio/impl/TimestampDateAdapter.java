package fi.vm.sade.haku.oppija.common.organisaatio.impl;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class TimestampDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String ts = json.getAsString();
        if (isBlank(ts)) {
            return null;
        }
        return new Date(Long.parseLong(ts));
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {

        if (src != null) {
            return new JsonPrimitive(String.valueOf(src.getTime()));
        }

        return JsonNull.INSTANCE;
    }
}
