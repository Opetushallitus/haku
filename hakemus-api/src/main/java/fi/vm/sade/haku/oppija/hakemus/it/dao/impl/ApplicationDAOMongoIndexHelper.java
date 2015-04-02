package fi.vm.sade.haku.oppija.hakemus.it.dao.impl;

import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoConstants.*;

final class ApplicationDAOMongoIndexHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDAOMongoIndexHelper.class);

    static String addIndexHint(final DBObject query) {

        final String queryString = query.toString();

        boolean hasApplicationState = queryString.contains(FIELD_APPLICATION_STATE);
        boolean hasApplicationSystemId = queryString.contains(FIELD_APPLICATION_SYSTEM_ID);
        boolean hasAo = queryString.contains(META_FIELD_AO);

        if (hasAo) {
            if (hasApplicationState) {
                if (hasApplicationSystemId)
                    return LogAndReturnHint(queryString, INDEX_STATE_ASID_AO_OID);
                else
                    return LogAndReturnHint(queryString, INDEX_STATE_AO_OID);
            } else {
                if (hasApplicationSystemId)
                    return LogAndReturnHint(queryString, INDEX_ASID_AO_OID);
                else
                    return LogAndReturnHint(queryString, INDEX_AO_OID);
            }
        }

        boolean hasAllOrgs = queryString.contains(META_ALL_ORGANIZATIONS)
                && !queryString.contains(META_FIELD_OPO_ALLOWED)
                && !queryString.contains(FIELD_SSN);

        if (hasAllOrgs) {
            if (hasApplicationState) {
                if (hasApplicationSystemId)
                    return LogAndReturnHint(queryString, INDEX_STATE_ASID_ORG_OID);
                else
                    return LogAndReturnHint(queryString, INDEX_STATE_ORG_OID);

            } else {
                if (hasApplicationSystemId)
                    return LogAndReturnHint(queryString, INDEX_ASID_ORG_OID);
                else
                    return LogAndReturnHint(queryString, INDEX_ORG_OID);
            }
        }
        if (hasApplicationSystemId) {
            if (hasApplicationState)
                return LogAndReturnHint(queryString, INDEX_STATE_ASID_FN);
            else
                return LogAndReturnHint(queryString, INDEX_APPLICATION_SYSTEM_ID);
        }
        if (hasApplicationState)
            return LogAndReturnHint(queryString, INDEX_STATE_FN);
        return LogAndReturnHint(queryString, null);
    }

    private static String LogAndReturnHint(final String query, final String index) {
        LOG.info("Chose: {} for query: {}", index, query);
        return index;
    }
}
