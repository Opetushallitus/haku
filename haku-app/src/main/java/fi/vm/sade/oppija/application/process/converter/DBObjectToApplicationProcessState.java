package fi.vm.sade.oppija.application.process.converter;

import com.google.common.base.Function;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessState;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * @author Mikko Majapuro
 */
public class DBObjectToApplicationProcessState implements Function<DBObject, ApplicationProcessState> {

    @Override
    public ApplicationProcessState apply(final DBObject dbObject) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        return mapper.convertValue(dbObject.toMap(), ApplicationProcessState.class);
    }
}
