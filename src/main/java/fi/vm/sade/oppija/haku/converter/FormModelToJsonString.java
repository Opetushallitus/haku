package fi.vm.sade.oppija.haku.converter;

import com.mongodb.util.JSON;
import fi.vm.sade.oppija.haku.domain.FormModel;
import org.springframework.core.convert.converter.Converter;

/**
 * @author jukka
 * @version 9/14/123:44 PM}
 * @since 1.1
 */
public class FormModelToJsonString implements Converter<FormModel, String> {
    @Override
    public String convert(FormModel formModel) {
        return JSON.serialize(new FormModelToMapConverter().convert(formModel));
    }
}
