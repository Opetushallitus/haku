package fi.vm.sade.haku.oppija.common.dao;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import fi.vm.sade.haku.oppija.hakemus.it.dao.impl.MongoIndexHelper;
import org.junit.Test;

import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MongoIndexHelperTest {

    @Test
    public void includeNestedAnd() {
        final String nestedAndKey1 = "the.nested.and.key1";
        final String nestedAndKey2 = "the.nested.and.key2";
        final String andKey = "the.and.key";
        final DBObject query = QueryBuilder.start()
                .and(
                        QueryBuilder.start(andKey).is("and value").get(),
                        QueryBuilder.start().and(
                                QueryBuilder.start(nestedAndKey1).is("nested and val1").get(),
                                QueryBuilder.start(nestedAndKey2).is("nested and val2").get()
                        ).get()
                ).get();
        final Set<String> indexFields = MongoIndexHelper.findIndexFields(query);
        assertTrue(indexFields.remove(andKey));
        assertTrue(indexFields.remove(nestedAndKey1));
        assertTrue(indexFields.remove(nestedAndKey2));
        assertEquals("Should not contain more fields", 0, indexFields.size());
    }

    @Test
    public void skipOrIncludeAnd() {
        final String orKey = "the.or.key";
        final String orAndKey1 = "the.or.and.key1";
        final String orAndKey2 = "the.or.and.key2";
        final String andKey = "the.and.key";
        final DBObject query = QueryBuilder.start()
                .and(
                        QueryBuilder.start(andKey).is("and value").get(),
                        QueryBuilder.start()
                                .or(
                                        QueryBuilder.start(orKey).is("or value").get(),
                                        QueryBuilder.start().and(
                                                QueryBuilder.start(orAndKey1).is("and or val1").get(),
                                                QueryBuilder.start(orAndKey2).is("and or val2").get()
                                        ).get()
                                ).get()
                ).get();
        final Set<String> indexFields = MongoIndexHelper.findIndexFields(query);
        assertTrue(indexFields.remove(andKey));
        assertEquals("Should not contain more fields", 0, indexFields.size());
    }

    @Test
    public void skipNonIndexQueryExists() {
        final String key = "the.key";
        final DBObject query = QueryBuilder.start(key).exists(true).get();
        final Set<String> indexFields = MongoIndexHelper.findIndexFields(query);
        assertEquals("Should not contain more fields", 0, indexFields.size());
    }

    @Test
    public void regexPattern() {
        final String key = "the.key";
        final DBObject query = QueryBuilder.start(key).regex(Pattern.compile("^Something")).get();
        final Set<String> indexFields = MongoIndexHelper.findIndexFields(query);
        assertTrue(indexFields.remove(key));
        assertEquals("Should not contain more fields", 0, indexFields.size());
    }

    @Test
    public void regexSubquery() {
        final String key = "the.key";
        final DBObject query = new BasicDBObject(key, new BasicDBObject("$regexp", "^Something"));
        final Set<String> indexFields = MongoIndexHelper.findIndexFields(query);
        assertTrue(indexFields.remove(key));
        assertEquals("Should not contain more fields", 0, indexFields.size());
    }

    @Test
    public void skipRegexScan() {
        final String key = "the.key";
        final DBObject query = QueryBuilder.start(key).regex(Pattern.compile("No Start in pattern")).get();
        final Set<String> indexFields = MongoIndexHelper.findIndexFields(query);
        assertEquals("Should not contain more fields", 0, indexFields.size());
    }

    @Test
    public void elemMatchWithAnd() {
        final String key1 = "a.key";
        final String matchPath = "some.path";
        final String elemKey1 = "elem.key1";
        final String elemKey2 = "elem.key2";

        final DBObject query = QueryBuilder.start()
                .and(
                        QueryBuilder.start(key1).is("some value").get(),
                        QueryBuilder.start(matchPath)
                                .elemMatch(QueryBuilder.start()
                                                .and(
                                                        QueryBuilder.start(elemKey1).in(Lists.newArrayList("in1", "in2")).get(),
                                                        QueryBuilder.start(elemKey2).lessThan(3).get()
                                                ).get()
                                ).get()
                ).get();

        final Set<String> indexFields = MongoIndexHelper.findIndexFields(query);

        assertTrue(indexFields.remove(key1));
        assertTrue(indexFields.remove(matchPath + "." + elemKey1));
        assertTrue(indexFields.remove(matchPath + "." + elemKey2));
        assertEquals("Should not contain more fields", 0, indexFields.size());
    }

}
