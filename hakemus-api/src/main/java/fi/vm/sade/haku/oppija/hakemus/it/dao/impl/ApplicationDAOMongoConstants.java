package fi.vm.sade.haku.oppija.hakemus.it.dao.impl;

import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

public final class ApplicationDAOMongoConstants {
    static final String INDEX_APPLICATION_OID = "index_oid";
    static final String INDEX_APPLICATION_SYSTEM_ID = "index_as_oid";
    static final String INDEX_SSN_DIGEST_SEARCH = "index_Henkilotunnus_digest_search";
    static final String INDEX_SSN_DIGEST = "index_Henkilotunnus_digest";
    static final String INDEX_DATE_OF_BIRTH = "index_syntymaaika";
    static final String INDEX_PERSON_OID = "index_personOid";
    static final String INDEX_EMAIL = "index_email";

    static final String INDEX_SENDING_SCHOOL = "index_lahtokoulu";
    static final String INDEX_SEARCH_NAMES = "index_searchNames";
    static final String INDEX_FULL_NAME = "index_full_name";
    static final String INDEX_MODEL_VERSION = "index_model_version";
    static final String INDEX_ASID_SENDING_SCHOOL_AND_FULL_NAME = "index_asid_sending_school_and_full_name";
    static final String INDEX_ASID_AND_SENDING_SCHOOL = "index_asid_and_sending_school";

    static final String INDEX_STATE_ASID_FN = "index_state_asid_fn";
    static final String INDEX_STATE_ASID_AO_OID = "index_state_asid_ao_org_oid";
    static final String INDEX_STATE_AO_OID = "index_state_ao_oid";
    static final String INDEX_ASID_AO_OID = "index_asid_ao_oid";
    static final String INDEX_AO_OID = "index_ao_oid";
    static final String INDEX_STATE_ASID_ORG_OID = "index_state_asid_org_oid";
    static final String INDEX_STATE_ORG_OID = "index_state_org_oid";
    static final String INDEX_ASID_ORG_OID = "index_asid_org_oid";
    static final String INDEX_ORG_OID = "index_org_oid";
    static final String INDEX_PAYMENT_DUE_DATE = "index_payment_due_date";
    static final String INDEX_RECEIVED_UPDATED = "index_received_updated";

    //Reference fields
    static final String FIELD_APPLICATION_SYSTEM_ID = "applicationSystemId";
    static final String FIELD_APPLICATION_OID = "oid";
    static final String FIELD_PERSON_OID = "personOid";
    static final String FIELD_STUDENT_OID = "studentOid";

    static final String META_FIELD_OPO_ALLOWED = "authorizationMeta.opoAllowed";
    static final String META_LOP_PARENTS_T = "authorizationMeta.aoOrganizations.%d";
    static final String META_SENDING_SCHOOL_PARENTS = "authorizationMeta.sendingSchool";
    static final String META_ALL_ORGANIZATIONS = "authorizationMeta.allAoOrganizations";

    static final String FIELD_AO_T = "answers.hakutoiveet.preference%d-Koulutus-id";
    static final String META_FIELD_AO = "authorizationMeta.applicationPreference.preferenceDataKoulutus-id";

    static final String FIELD_AO_KOULUTUS_ID_T = "answers.hakutoiveet.preference%d-Koulutus-id-aoIdentifier";

    static final String FIELD_DISCRETIONARY_T = "answers.hakutoiveet.preference%d-discretionary";
    static final String FIELD_AO_GROUPS_T = "answers.hakutoiveet.preference%d-Koulutus-id-ao-groups";

    //TODO Meta
    static final String FIELD_HIGHER_ED_BASE_ED_T = "answers.koulutustausta.pohjakoulutus_%s";

    // Application Answers
    static final String FIELD_SENDING_SCHOOL = "answers.koulutustausta.lahtokoulu";
    static final String FIELD_SENDING_CLASS = "answers.koulutustausta.lahtoluokka";
    static final String FIELD_CLASS_LEVEL = "answers.koulutustausta.luokkataso";

    static final String FIELD_EMAIL = "answers.henkilotiedot." + OppijaConstants.ELEMENT_ID_EMAIL;

    static final String FIELD_SSN = "answers.henkilotiedot.Henkilotunnus";
    static final String FIELD_SSN_DIGEST = "answers.henkilotiedot.Henkilotunnus_digest";
    static final String FIELD_DATE_OF_BIRTH = "answers.henkilotiedot.syntymaaika";

    // Processing information fields
    static final String FIELD_APPLICATION_STATE = "state";
    static final String FIELD_REQUIRED_PAYMENT_STATE = "requiredPaymentState";
    static final String FIELD_RECEIVED = "received";
    static final String FIELD_UPDATED = "updated";

    //Technical fields
    static final String FIELD_SEARCH_NAMES = "searchNames";
    static final String FIELD_FULL_NAME = "fullName";
    static final String FIELD_MODEL_VERSION = "modelVersion";
    static final String FIELD_APPLICATION_VERSION = "version";
    static final String FIELD_STUDENT_IDENTIFICATION_DONE = "studentIdentificationDone";
    static final String FIELD_REDO_POSTPROCESS = "redoPostProcess";
    static final String FIELD_LAST_AUTOMATED_PROCESSING_TIME = "lastAutomatedProcessingTime";

    // Preference Eligibilities
    static final String FIELD_PREFERENCE_ELIGIBILITIES = "preferenceEligibilities";
    static final String FIELD_STATUS = "status";
    static final String FIELD_PREFERENCE_ELIGIBILITY_AO_OID = "aoId";

    // Change history
    static final String FIELD_HISTORY = "history";
    static final String FIELD_CHANGES = "changes";
    static final String FIELD_CHANGE_FIELD = "field";
}
