package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ThemeQuestionToDBObjectFunction implements Function<ThemeQuestion, DBObject>{

    @Override
    public DBObject apply(ThemeQuestion themeQuestion) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        mapper.disable(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS);
        mapper.disable(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES);

        @SuppressWarnings("rawtypes")
        final Map m = mapper.convertValue(themeQuestion, Map.class);
        final BasicDBObject basicDBObject = new BasicDBObject(m);
        return basicDBObject;
    }
}
