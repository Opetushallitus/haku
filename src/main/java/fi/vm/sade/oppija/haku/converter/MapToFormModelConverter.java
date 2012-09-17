package fi.vm.sade.oppija.haku.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fi.vm.sade.oppija.haku.domain.FormModel;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

/**
 * @author jukka
 * @version 9/12/125:23 PM}
 * @since 1.1
 */
public class MapToFormModelConverter implements Converter<Map, FormModel> {
    @Override
    public FormModel convert(Map map) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.convertValue(map, FormModel.class);
    }
}
