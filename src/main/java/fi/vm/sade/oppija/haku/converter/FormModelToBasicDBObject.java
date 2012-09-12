package fi.vm.sade.oppija.haku.converter;

import com.mongodb.BasicDBObject;
import fi.vm.sade.oppija.haku.domain.FormModel;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

/**
 * @author jukka
 * @version 9/12/124:29 PM}
 * @since 1.1
 */

public class FormModelToBasicDBObject implements Converter<FormModel, BasicDBObject> {

    private final FormModelToMapConverter formModelToMapConverter = new FormModelToMapConverter();

    @Override
    public BasicDBObject convert(FormModel formModel) {
        Map formModelMap = formModelToMapConverter.convert(formModel);
        return new BasicDBObject(formModelMap);
    }


}
