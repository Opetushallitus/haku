package fi.vm.sade.haku.healthcheck;

import com.google.common.base.Preconditions;
import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
@Profile(value = {"default", "devluokka", "vagrant"})
public class StatusRepositoryImpl implements StatusRepository {

    private static final String FIELD_HOST= "host";
    private static final String FIELD_OPERATION= "operation";
    private static final String FIELD_TIMESTAMP= "ts";

    private static final Logger log = LoggerFactory.getLogger(StatusRepositoryImpl.class);

    private static final String STATUS_COLLECTION = "systemStatus";
    private static final DateFormat tsFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final MongoOperations mongo;

    @Value("${server.name}")
    private String serverName;

    @Value("${mongodb.ensureIndex:true}")
    private boolean ensureIndex;

    @Autowired
    public StatusRepositoryImpl(final MongoOperations mongoOperations) {
        this.mongo = mongoOperations;
    }

    @Override
    public List<Map<String, String>> read() {
        DBCursor cursor = mongo.getCollection(STATUS_COLLECTION).find();
        List<Map<String, String>> statuses = new ArrayList<Map<String, String>>();
        while (cursor.hasNext()) {
            statuses.add(statusToMap(cursor.next()));
        }
        return statuses;
    }

    @Override
    public void write(final String operation) {
        write(operation, null);
    }

    @Override
    public void write(final String operation, final Map<String, String> statusData) {
        Preconditions.checkNotNull(operation, "Operation can not be null");

        final DBObject query = new BasicDBObject(FIELD_HOST, serverName).append(FIELD_OPERATION, operation);

        final DBObject newObject =  new BasicDBObject(FIELD_HOST, serverName).append(FIELD_OPERATION, operation);
        if (statusData != null && !statusData.isEmpty()) {
            newObject.putAll(statusData);
        }
        newObject.put(FIELD_TIMESTAMP, new Date());

        final WriteResult result = this.mongo.getCollection(STATUS_COLLECTION).update(query, newObject, true, false, WriteConcern.ACKNOWLEDGED);
        final String error = result.getError();
        if (isNotBlank(error)) {
            log.error("Writing systemStatus failed: {}", error);
        }
    }

    private Map<String, String> statusToMap(final DBObject status) {
        final Map<String, String> statusMap = new HashMap<>();
        for (String key : status.keySet()) {
            Object value = status.get(key);
            String valueStr = null;
            if (key.equals("_id")) {
                valueStr = value.toString();
            } else if (key.equals("ts")) {
                valueStr = tsFmt.format((Date) value);
            } else {
                valueStr = (String) value;
            }
            statusMap.put(key, valueStr);
        }
        return statusMap;
    }

    @PostConstruct
    void initIndexes(){
        if (!ensureIndex)
            return;
        this.mongo.getCollection(STATUS_COLLECTION).ensureIndex(new BasicDBObject(FIELD_HOST, 1).append(FIELD_OPERATION, 1),"index_update");
    }
}
