package fi.vm.sade.haku.oppija.hakemus.it.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import org.junit.Test;

import java.util.Collections;

import static fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoConstants.*;
import static org.junit.Assert.assertEquals;

public class ApplicationDAOMongoIndexHelperTest {
    @Test
    public void shouldChooseCorrectIndexHint() {
        final DBObject query = QueryBuilder.start()
                .and(
                        QueryBuilder.start().or(
                                QueryBuilder.start(FIELD_RECEIVED).greaterThan(1).get(),
                                QueryBuilder.start(FIELD_UPDATED).greaterThan(1).get()
                        ).get(),
                        QueryBuilder.start(META_ALL_ORGANIZATIONS).in(Collections.singletonList("1.2.3")).get()
                ).get();

        assertEquals(INDEX_RECEIVED_UPDATED, ApplicationDAOMongoIndexHelper.addIndexHint(query, new BasicDBObject()));
    }

}
