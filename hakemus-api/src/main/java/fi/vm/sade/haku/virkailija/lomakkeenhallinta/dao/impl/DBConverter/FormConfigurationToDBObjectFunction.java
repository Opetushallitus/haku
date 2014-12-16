package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter;

import com.google.common.base.Function;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FormConfigurationToDBObjectFunction implements Function<FormConfiguration, DBObject>{

    private static final String FORM_CONFIGURATION_ID = "_id";

    private final ObjectMapper mapper;

    public FormConfigurationToDBObjectFunction(){
        mapper = new ObjectMapper();
        mapper.disable(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS);
        mapper.disable(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES);
    }

    @Override
    public DBObject apply(FormConfiguration themeQuestion) {
        final Map m = mapper.convertValue(themeQuestion, Map.class);

        //Dirty hack
        Object id = m.get(FORM_CONFIGURATION_ID);
        if (null != id && id instanceof String) {
            m.put(FORM_CONFIGURATION_ID, new ObjectId(id.toString()));
        }
        final BasicDBObject basicDBObject = new BasicDBObject(m);
        return basicDBObject;
    }
}
