package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter;

import com.google.common.base.Function;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ThemeQuestionToDBObjectFunction implements Function<ThemeQuestion, DBObject>{

    private static final String THEMEQUESTION_ID = "_id";

    private final ObjectMapper mapper;

    public ThemeQuestionToDBObjectFunction(){
        mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        mapper.disable(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS);
        mapper.disable(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES);
    }

    @Override
    public DBObject apply(ThemeQuestion themeQuestion) {
        final Map m = mapper.convertValue(themeQuestion, Map.class);

        //Dirty hack
        Object id = m.get(THEMEQUESTION_ID);
        if (null != id && id instanceof String) {
            m.put(THEMEQUESTION_ID, new ObjectId(id.toString()));
        }
        final BasicDBObject basicDBObject = new BasicDBObject(m);
        return basicDBObject;
    }
}
