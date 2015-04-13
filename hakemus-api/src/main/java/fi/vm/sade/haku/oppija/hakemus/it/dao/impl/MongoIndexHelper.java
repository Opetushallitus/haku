package fi.vm.sade.haku.oppija.hakemus.it.dao.impl;

import com.google.common.collect.Lists;
import com.mongodb.DBObject;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.BSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

import static com.mongodb.QueryOperators.*;

public class MongoIndexHelper {

    private static final Logger LOG = LoggerFactory.getLogger(MongoIndexHelper.class);

    private static final ArrayList<String> INDEX_OPERATION = Lists.newArrayList(GT, GTE, LT, LTE, IN);
    private static final ArrayList<String> NON_INDEX_OPERATION = Lists.newArrayList(NE, NIN, EXISTS, NOT);
    private static final String OP_REGEXP = "$regexp";
    private static final String REGEX_INPUT_BEGIN = "^";


    public static Set<String> findIndexFields(final DBObject query){
        return findAndAddIndexFields(query.toMap(), null);
    }


    private static HashSet<String> findAndAddIndexFields(final Map<String, Object> fromQuery, HashSet<String> toFieldSet) {
        if (null == toFieldSet){
            toFieldSet = new HashSet<>();
        }

        for (final Map.Entry<String, Object> entry : fromQuery.entrySet()) {
            final String key = entry.getKey();

            if (null == key || OR.equals(key) || NOR.equals(key)) {
                // skip
                continue;
            }

            if (AND.equals(key)) {
                final Object value = entry.getValue();
                if (!List.class.isInstance(value)) {
                    logPanic("Was expecting a list", key, value);
                    continue;
                }
                for (final Object subQuery : (List) value){
                    final Map valMap = getMap(subQuery);
                    if (null != valMap) {
                        findAndAddIndexFields(valMap, toFieldSet);
                    }
                    else {
                        logPanic("Cannot handle subquery.", key, subQuery);
                        continue;
                    }
                }
            } else {
                final Object value = entry.getValue();
                final Map valMap = getMap(value);
                if (null != valMap){
                    addFromQueryStructure(key, valMap, toFieldSet);
                } else {
                    if (!Pattern.class.isInstance(value) || patternMatchesBeginingOfLine(value.toString()))
                        toFieldSet.add(key);
                    else
                        logPanic("Possibly non-index pattern", key, value);
                }
            }
        }
        return toFieldSet;
    }

    private static void addFromQueryStructure(final String key, final Map<String, Object> fromQuery, final HashSet<String> toFieldSet) {
        if (fromQuery.size() != 1) {
            logPanic("Expected one and only one element.", key, fromQuery);
            return;
        }

        final Map.Entry<String, Object> entry = (Map.Entry<String, Object>) fromQuery.entrySet().toArray()[0];
        final String operation = entry.getKey();
        final Object value = entry.getValue();
        if (ELEM_MATCH.equals(operation)) {
            final HashSet<String> subQueryKeys = findAndAddIndexFields(getMap(value), null);
            final String prefix = key + ".";
            for (final String subQueryKey : subQueryKeys) {
                toFieldSet.add(prefix+subQueryKey);
            }
        } else if (OP_REGEXP.equals(operation)) {
            if(patternMatchesBeginingOfLine(value.toString()))
                toFieldSet.add(key);
            else
                logPanic("Possibly non-index pattern", key, value);
        } else if (INDEX_OPERATION.contains(operation)) {
            toFieldSet.add(key);
        } else if (NON_INDEX_OPERATION.contains(operation)) {
            return;
        } else {
            logPanic("Unknown combination", key, fromQuery);
        }
    }

    private static boolean patternMatchesBeginingOfLine(final String pattern){
        return null != pattern && pattern.startsWith(REGEX_INPUT_BEGIN);
    }

    private static Map getMap(final Object mapCandidate) {
        if (BSONObject.class.isInstance(mapCandidate))
            return ((BSONObject) mapCandidate).toMap();
        if (Map.class.isInstance(mapCandidate)) {
            return (Map) mapCandidate;
        }
        return null;
    }

    private static void logPanic(final String description, final Object key, final Object value) {
        LOG.error("Don't know what to do. {} Input key {}, value {}", description, ReflectionToStringBuilder.toString(key), ReflectionToStringBuilder.toString(value));
    }
}
