package fi.vm.sade.oppija.application.process.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Function;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessState;

/**
 * @author Mikko Majapuro
 */
public class DBObjectToApplicationProcessState implements Function<DBObject, ApplicationProcessState> {

    @Override
    public ApplicationProcessState apply(final DBObject dbObject) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.convertValue(dbObject.toMap(), ApplicationProcessState.class);
    }
}
