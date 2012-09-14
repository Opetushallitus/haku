package fi.vm.sade.oppija.haku.converter;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.haku.domain.FormModel;
import org.springframework.core.convert.converter.Converter;

/**
 * @author jukka
 * @version 9/14/121:59 PM}
 * @since 1.1
 */
public class JsonStringToFormModelConverter implements Converter<String, FormModel> {
    @Override
    public FormModel convert(String json) {
        final DBObject dbObject = (DBObject) JSON.parse(json);
        return new MapToFormModelConverter().convert((dbObject.toMap()));
    }
}
