package fi.vm.sade.haku.oppija.lomake.domain;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static fi.vm.sade.haku.oppija.common.mongo.DBObjectUtils.*;


/**
 * Historically "form" has been large enough to break MongoDB 16 MB limit.
 * When persisted by MongoTemplate, this listener now de/compresses the field
 * transparently.
 */
@Component
public class ApplicationSystemMongoEventListener extends AbstractMongoEventListener<ApplicationSystem> {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationSystemMongoEventListener.class);
    private static final String FORM_FIELD = "form";

    /**
     * Load will let non-compressed data pass through as such, i.e. no
     * migration needed for existing data.
     */
    @Override
    public void onAfterLoad(DBObject dbo) {
        try {
            Object data = dbo.get(FORM_FIELD);
            if (data != null && data.getClass().equals(byte[].class)) {
                byte[] compressedBinary = (byte[]) data;
                if (isGZIP(compressedBinary)) {
                    dbo.put(FORM_FIELD, decompressDBObject(compressedBinary));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Failed to decompress form", e);
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void onBeforeSave(ApplicationSystem source, DBObject dbo) {
        try {
            BasicDBObject form = (BasicDBObject) dbo.get(FORM_FIELD);
            if (form != null) {
                dbo.put(FORM_FIELD, compressDBObject(form));
            }
        } catch (IOException e) {
            logger.error("Failed to compress form", e);
            throw new IllegalArgumentException(e);
        }
    }
}
