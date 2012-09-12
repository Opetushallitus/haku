package fi.vm.sade.oppija.haku.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import fi.vm.sade.oppija.haku.domain.FormModel;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.util.Map;

/**
 * @author jukka
 * @version 9/12/124:29 PM}
 * @since 1.1
 */

public class FormModelToBasicDBObject implements Converter<FormModel, BasicDBObject> {

    @Override
    public BasicDBObject convert(FormModel formModel) {
        try {
            Map formModelMap = serialize(formModel, Map.class);
            return new BasicDBObject(formModelMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map serialize(FormModel model, Class<Map> dbObjectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(model, dbObjectClass);
    }

}
