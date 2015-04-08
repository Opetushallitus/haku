package fi.vm.sade.haku.oppija.hakemus.it.dao.impl;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;

import static com.mongodb.QueryOperators.IN;
import static fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoConstants.*;

final class ApplicationDAOMongoPostProcessingQueries {

    static final String INDEX_POSTPROCESS = "index_postprocess";

    static final String[] INDEX_POSTPROCESS_FIELDS = {
            FIELD_APPLICATION_STATE,
            FIELD_REDO_POSTPROCESS,
            FIELD_LAST_AUTOMATED_PROCESSING_TIME
    };

    private static final BasicDBObject REDO_QUERY = new BasicDBObject(
            FIELD_APPLICATION_STATE, new BasicDBObject(IN,
            Lists.newArrayList(
                    Application.State.DRAFT.name(),
                    Application.State.ACTIVE.name(),
                    Application.State.INCOMPLETE.name())))
            .append(FIELD_REDO_POSTPROCESS, new BasicDBObject(IN, Lists.newArrayList(
                            Application.PostProcessingState.FULL.toString(),
                            Application.PostProcessingState.NOMAIL.toString())));

    private static final BasicDBObject SUBMITTED_QUERY = new BasicDBObject(
            FIELD_APPLICATION_STATE, Application.State.SUBMITTED.toString())
            .append(FIELD_REDO_POSTPROCESS, new BasicDBObject(IN, Lists.newArrayList(
                            null,
                            Application.PostProcessingState.FULL.toString(),
                            Application.PostProcessingState.NOMAIL.toString())));

    static final String INDEX_STUDENT_IDENTIFICATION_DONE = "index_studentIdentificationDone";

    static final String[] INDEX_STUDENT_IDENTIFICATION_DONE_FIELDS = {
            FIELD_APPLICATION_STATE,
            FIELD_STUDENT_IDENTIFICATION_DONE,
            FIELD_LAST_AUTOMATED_PROCESSING_TIME
    };

    private static final BasicDBObject IDENTIFICATION_QUERY = new BasicDBObject(
            FIELD_APPLICATION_STATE, new BasicDBObject(IN,
            Lists.newArrayList(
                    Application.State.ACTIVE.name(),
                    Application.State.INCOMPLETE.name())))
            .append(FIELD_STUDENT_IDENTIFICATION_DONE, false);

    static final DBObject buildRedoQuery() {
        return (BasicDBObject) REDO_QUERY.copy();
    }

    static final DBObject buildSubmittedQuery() {
        return (BasicDBObject) SUBMITTED_QUERY.copy();
    }

    static final DBObject buildIdentificationQuery() {
        return (BasicDBObject) IDENTIFICATION_QUERY.copy();
    }
}
