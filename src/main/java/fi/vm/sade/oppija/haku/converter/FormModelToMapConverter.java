package fi.vm.sade.oppija.haku.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

/**
 * @author jukka
 * @version 9/12/125:19 PM}
 * @since 1.1
 */
public class FormModelToMapConverter implements Converter<FormModel, Map> {
    @Override
    public Map convert(FormModel formModel) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(formModel, Map.class);
    }
}
