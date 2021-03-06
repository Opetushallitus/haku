package fi.vm.sade.haku.oppija.ui.controller.dto;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateDeserializer extends JsonDeserializer<Date> {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US); //Sun Feb 04 10:51:54 EET 2018

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String dateString = jsonParser.getText();
        try {
            return null == dateString ? null : dateFormat.parse(dateString);
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
    }
}
