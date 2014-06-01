package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter;

import com.google.common.base.Function;
import com.mongodb.DBObject;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DBObjectToThemeQuestionFunction implements Function<DBObject, ThemeQuestion> {

    private final ObjectMapper mapper;

    public DBObjectToThemeQuestionFunction(){
        mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        mapper.enable(DeserializationConfig.Feature.READ_ENUMS_USING_TO_STRING);
        mapper.enable(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING);
    }

    @Override
    public ThemeQuestion apply(DBObject dbObject) {
        final Map fromValue = dbObject.toMap();
        return mapper.convertValue(fromValue, ThemeQuestion.class);
    }

}
