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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
@Profile(value = {"default", "devluokka", "vagrant"})
public class StatusRepositoryImpl implements StatusRepository {

    private static final Logger log = LoggerFactory.getLogger(StatusRepositoryImpl.class);

    private static final String STATUS_COLLECTION = "systemStatus";
    private static final DateFormat tsFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final MongoOperations mongo;

    @Value("${server.name}")
    private String serverName;

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

        DBObject query = new BasicDBObject();
        query.put("host", serverName);
        query.put("operation", operation);

        DBObject newObject = new BasicDBObject();
        if (statusData != null && !statusData.isEmpty()) {
            newObject.putAll(statusData);
        }
        newObject.put("host", serverName);
        newObject.put("operation", operation);
        newObject.put("ts", new Date());

        WriteResult result = this.mongo.getCollection(STATUS_COLLECTION).update(query, newObject, true, false, WriteConcern.ACKNOWLEDGED);
        String error = result.getError();
        if (isNotBlank(error)) {
            log.error("Writing systemStatus failed: {}", error);
        }
    }

    private Map<String, String> statusToMap(DBObject status) {
        Map<String, String> statusMap = new HashMap<String, String>();
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
}
