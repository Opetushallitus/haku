package fi.vm.sade.haku.oppija.hakemus.it.dao.impl;

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
                        QueryBuilder.start(FIELD_UPDATED).greaterThan(1).get(),
                        QueryBuilder.start(META_ALL_ORGANIZATIONS).in(Collections.singletonList("1.2.3")).get()
                ).get();

        assertEquals(INDEX_UPDATED, ApplicationDAOMongoIndexHelper.addIndexHint(query));
    }

}
