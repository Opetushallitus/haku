package fi.vm.sade.haku.oppija.hakemus.it.dao.impl;

import com.google.common.collect.Sets;
import com.mongodb.DBObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoConstants.*;
import static fi.vm.sade.haku.oppija.hakemus.it.dao.impl.MongoIndexHelper.findIndexFields;

final class ApplicationDAOMongoIndexHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDAOMongoIndexHelper.class);

    private final DBObject query;
    private final Set<String> indexFields;
    private final Set<String> checkedFields;
    private final String indexCandidate;

    static String addIndexHint(final DBObject query) {
        final ApplicationDAOMongoIndexHelper indexHelper = new ApplicationDAOMongoIndexHelper(query);
        return indexHelper.getHint();
    }

    private ApplicationDAOMongoIndexHelper(final DBObject query) {
        this.query = query;
        this.indexFields = findIndexFields(query);
        this.checkedFields = Sets.newHashSetWithExpectedSize(indexFields.size());
        indexCandidate = initIndexCanditate();
    }

    private String initIndexCanditate() {
        if (hasAo()) {
            if (hasApplicationState()) {
                if (hasApplicationSystemId())
                    return INDEX_STATE_ASID_AO_OID;
                else
                    return INDEX_STATE_AO_OID;
            } else {
                if (hasApplicationSystemId())
                    return INDEX_ASID_AO_OID;
                else
                    return INDEX_AO_OID;
            }
        }

        if (hasAllOrgs()) {
            if (hasApplicationState()) {
                if (hasApplicationSystemId())
                    return INDEX_STATE_ASID_ORG_OID;
                else
                    return INDEX_STATE_ORG_OID;

            } else {
                if (hasApplicationSystemId())
                    return INDEX_ASID_ORG_OID;
                else
                    return INDEX_ORG_OID;
            }
        }
        if (hasApplicationSystemId()) {
            if (hasApplicationState())
                return INDEX_STATE_ASID_FN;
            else
                return INDEX_APPLICATION_SYSTEM_ID;
        }
        if (hasApplicationState())
            return INDEX_STATE_FN;
        return null;
    }

    // index key checkers

    private boolean hasApplicationState() {
        return checkKey(FIELD_APPLICATION_STATE);
    }

    private boolean hasApplicationSystemId() {
        return checkKey(FIELD_APPLICATION_SYSTEM_ID);
    }

    private boolean hasAo() {
        return checkKey(META_FIELD_AO);
    }

    private boolean hasAllOrgs() {
        return checkKey(META_ALL_ORGANIZATIONS);
    }

    // utilities

    private boolean checkKey(final String key){
        if (checkedFields.contains(key)) {
            LOG.error("Double check for key : " + key);
            return true;
        }
        if (indexFields.remove(key)) {
            checkedFields.add(key);
            return true;
        }
        return false;
    }

    private String getHint() {
        if (LOG.isDebugEnabled() && indexFields.size() > 0) {
            LOG.debug("From query {} unused fields {}", query, new ToStringBuilder(indexFields, ToStringStyle.SHORT_PREFIX_STYLE).append(indexFields.toArray()));
        }
        LOG.info("Chose: {} for query: {}", indexCandidate, query);
        return indexCandidate;
    }
}
