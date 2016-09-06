package fi.vm.sade.haku.oppija.hakemus.it.dao.impl;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import com.mongodb.QueryOperators;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationFilterParameters;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.haku.oppija.lomake.service.EncrypterService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static fi.vm.sade.haku.oppija.hakemus.domain.Application.State;
import static fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoConstants.*;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.*;

final class ApplicationDAOMongoQueryBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDAOMongoQueryBuilder.class);

    private static final Pattern OID_PATTERN = Pattern.compile("((^([0-9]{1,4}\\.){5})|(^))[0-9]{11}$");
    private static final Pattern HETU_PATTERN = Pattern.compile("^[0-3][0-9][0-1][0-9][0-9][0-9][-+Aa][0-9]{3}[0-9a-zA-Z]");

    private static final String REGEX_LINE_BEGIN = "^";

    private static final ArrayList<String> STATE_NOT_PASSIVE = Lists.newArrayList(State.DRAFT.name(), State.SUBMITTED.name(), State.ACTIVE.name(), State.INCOMPLETE.name());
    private static final ArrayList<String> STATE_CONSIDERED_ACTIVE = Lists.newArrayList(State.ACTIVE.name(), State.INCOMPLETE.name());

    private static final String OPERATOR_AND = "$and";
    private static final String OPERATOR_OR = "$or";

    private final EncrypterService shaEncrypter;
    private final String rootOrganizationOid;
    private final String applicationOidPrefix;
    private final String userOidPrefix;

    public ApplicationDAOMongoQueryBuilder(final EncrypterService shaEncrypter,
                                           final String rootOrganizationOid,
                                           final String applicationOidPrefix,
                                           final String userOidPrefix) {
        this.shaEncrypter = shaEncrypter;
        this.rootOrganizationOid = rootOrganizationOid;
        this.applicationOidPrefix = applicationOidPrefix;
        this.userOidPrefix = userOidPrefix;
    }

    public DBObject buildApplicationByApplicationOption(final String applicationSystemId, final String aoId, final ApplicationFilterParameters filterParameters) {
        final DBObject orgFilter = _filterByOrganization(filterParameters);
        return QueryBuilder.start().and(
                new BasicDBObject(META_FIELD_AO, aoId),
                new BasicDBObject(FIELD_APPLICATION_SYSTEM_ID, applicationSystemId),
                QueryBuilder.start(FIELD_APPLICATION_STATE).in(STATE_CONSIDERED_ACTIVE).get(),
                orgFilter).get();
    }
    public DBObject buildApplicationByApplicationOption(final List<String> oids, final ApplicationFilterParameters filterParameters) {
        final DBObject orgFilter = _filterByOrganization(filterParameters);
        return QueryBuilder.start().and(
                new BasicDBObject(FIELD_APPLICATION_OID, new BasicDBObject(QueryOperators.IN, oids)),
                QueryBuilder.start(FIELD_APPLICATION_STATE).in(STATE_CONSIDERED_ACTIVE).get(),
                orgFilter).get();
    }
    public DBObject buildApplicationExistsForSSN(final String ssn, final String asId) {
        return _buildApplicationExistsForSSN(ssn, asId).get();
    }

    public DBObject buildApplicationExistsForEmail(final String email, final String asId) {
        return _buildApplicationExistsForEmail(email, asId).get();
    }

    public DBObject buildApplicationExistsForSSN(final String ssn, final String asId, final String aoId) {
        return _buildApplicationExistsForSSN(ssn, asId)
                .and(META_FIELD_AO).is(aoId)
                .get();
    }

    private QueryBuilder _buildApplicationExistsForSSN(final String ssn, final String asId) {
        final String encryptedSsn = shaEncrypter.encrypt(ssn.toUpperCase());
        return QueryBuilder.start(FIELD_APPLICATION_SYSTEM_ID).is(asId)
                .and("answers.henkilotiedot." + SocialSecurityNumber.HENKILOTUNNUS_HASH).is(encryptedSsn)
                .and(FIELD_APPLICATION_STATE).in(STATE_NOT_PASSIVE);
    }

    private QueryBuilder _buildApplicationExistsForEmail(final String email, final String asId) {
        return QueryBuilder.start(FIELD_APPLICATION_SYSTEM_ID).is(asId)
                .and(FIELD_EMAIL).is(email)
                .and(FIELD_APPLICATION_STATE).in(STATE_NOT_PASSIVE);
    }

    public DBObject buildFindAllQuery(ApplicationQueryParameters applicationQueryParameters,
                                      ApplicationFilterParameters filterParameters) {
        long startTime = 0;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering buildQuery");
            startTime = System.currentTimeMillis();
        }
        final DBObject orgFilter = _filterByOrganization(filterParameters);
        if (null == orgFilter) {
            return QueryBuilder.start("_id").is(null).get();
        }

        final  ArrayList<DBObject> filters =_buildQueryFilter(applicationQueryParameters, filterParameters);
        final ArrayList<DBObject> queries = new ArrayList<>(3 + filters.size());
        // doing tricks to retain old order. Feel free to refactor later
        final DBObject searchTermQuery =_createSearchTermQuery(applicationQueryParameters);
        if (null != searchTermQuery)
            queries.add(searchTermQuery);
        if (filters.size() > 0) {
            queries.addAll(filters);
        }
        queries.add(orgFilter);

        final DBObject query = QueryBuilder.start().and(queries.toArray(new DBObject[queries.size()])).get();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Constructed query: {}. Took: {} ms", query.toString(), System.currentTimeMillis() - startTime);
        }
        return query;
    }

    private final DBObject _createSearchTermQuery(final ApplicationQueryParameters applicationQueryParameters) {
        final StringTokenizer tokenizedSearchTerms = new StringTokenizer(applicationQueryParameters.getSearchTerms(), " ");
        final ArrayList<DBObject> queries = new ArrayList<>();
        while (tokenizedSearchTerms.hasMoreTokens()) {
            final String searchTerm = tokenizedSearchTerms.nextToken();
            LOG.debug("processing token: {}", searchTerm);
            if (OID_PATTERN.matcher(searchTerm).matches()) {
                if (searchTerm.indexOf('.') > -1) { // Long form
                    if (searchTerm.startsWith(applicationOidPrefix)) {
                        queries.add(new BasicDBObject(FIELD_APPLICATION_OID, searchTerm));
                    } else if (searchTerm.startsWith(userOidPrefix)) {
                        queries.add(new BasicDBObject(FIELD_PERSON_OID, searchTerm));
                    } else {
                        queries.add(_createDobOrNameQuery(searchTerm));
                    }
                } else { // Short form
                    queries.add(
                            QueryBuilder.start().or(
                                    QueryBuilder.start(FIELD_APPLICATION_OID).is(applicationOidPrefix + "." + searchTerm).get(),
                                    QueryBuilder.start(FIELD_PERSON_OID).is(userOidPrefix + "." + searchTerm).get()
                            ).get()
                    );
                }
            } else if (HETU_PATTERN.matcher(searchTerm).matches()) {
                String encryptedSsn = shaEncrypter.encrypt(searchTerm.toUpperCase());
                queries.add(
                        QueryBuilder.start(FIELD_SSN_DIGEST).is(encryptedSsn).get()
                );
            } else { // Name or date of birth
                queries.add(_createDobOrNameQuery(searchTerm));
            }
        }

        return _combineQueries(OPERATOR_AND, queries);
    }

    private DBObject _createDobOrNameQuery(final String searchTerm) {
        final String possibleDob = searchTerm.replace(".", "");
        Date dob = tryDate(new SimpleDateFormat("ddMMyy"), possibleDob);
        if (dob == null) {
            dob = tryDate(new SimpleDateFormat("ddMMyyyy"), possibleDob);
        }
        if (dob != null) {
            return QueryBuilder.start(FIELD_DATE_OF_BIRTH).is(new SimpleDateFormat("dd.MM.yyyy").format(dob)).get();
        }
        return QueryBuilder.start(FIELD_SEARCH_NAMES).regex(Pattern.compile(REGEX_LINE_BEGIN + searchTerm.toLowerCase())).get();
    }

    private Date tryDate(DateFormat df, String str) {
        Date date = null;
        try {
            date = df.parse(str);
        } catch (ParseException pe) {
            // NOP
        }
        return date;
    }

    private ArrayList<DBObject> _buildQueryFilter(final ApplicationQueryParameters applicationQueryParameters,
                                         final ApplicationFilterParameters filterParameters) {
        final ArrayList<DBObject> filters = new ArrayList<>();
        final DBObject preferenceQuery = _createPreferenceFilters(applicationQueryParameters, filterParameters);

        if (null != preferenceQuery)
            filters.add(preferenceQuery);

        final List<String> aoOids = applicationQueryParameters.getAoOids();

        final Boolean preferenceChecked = applicationQueryParameters.getPreferenceChecked();
        if (preferenceChecked != null) {
            filters.add(
                    QueryBuilder.start("preferencesChecked").elemMatch(
                            aoOidElemMatcher(aoOids, QueryBuilder.start("checked").is(preferenceChecked), "preferenceAoOid")
                    ).get()
            );
        }

        final String preferenceEligibility = applicationQueryParameters.getPreferenceEligibility();
        if (preferenceEligibility != null) {
            filters.add(
                    QueryBuilder.start(FIELD_PREFERENCE_ELIGIBILITIES).elemMatch(
                            aoOidElemMatcher(aoOids, QueryBuilder.start(FIELD_STATUS).is(preferenceEligibility), FIELD_PREFERENCE_ELIGIBILITY_AO_OID)
                    ).get()
            );
        }

        final List<String> oids = applicationQueryParameters.getOids();
        if(oids != null && !oids.isEmpty()) {
            filters.add(QueryBuilder.start(FIELD_APPLICATION_OID).in(oids).get());
        }
        // Koskee koko hakemusta
        final List<String> states = applicationQueryParameters.getState();
        if (states != null && !states.isEmpty()) {
            if (states.size() == 1) {
                if ("NOT_IDENTIFIED".equals(states.get(0))) {
                    filters.add(QueryBuilder.start(FIELD_STUDENT_OID).is(null).get());
                } else if ("NO_SSN".equals(states.get(0))) {
                    filters.add(QueryBuilder.start(FIELD_SSN).is(null).get());
                } else if ("POSTPROCESS_FAILED".equals(states.get(0))) {
                    filters.add(QueryBuilder.start(FIELD_REDO_POSTPROCESS).is(Application.PostProcessingState.FAILED.toString()).get());
                } else {
                    filters.add(QueryBuilder.start(FIELD_APPLICATION_STATE).is(Application.State.valueOf(states.get(0)).toString()).get());
                }
            } else {
                filters.add(QueryBuilder.start(FIELD_APPLICATION_STATE).in(states).get());
            }
        }

        final String paymentState = applicationQueryParameters.getPaymentState();
        if (paymentState != null) {
            filters.add(QueryBuilder.start(FIELD_REQUIRED_PAYMENT_STATE).is(paymentState).get());
        }

        final List<String> asIds = applicationQueryParameters.getAsIds();
        if (asIds != null && !asIds.isEmpty()) {
            filters.add(QueryBuilder.start(FIELD_APPLICATION_SYSTEM_ID).in(asIds).get());
        }

        final String sendingSchool = applicationQueryParameters.getSendingSchool();
        if (!isEmpty(sendingSchool)) {
            filters.add(QueryBuilder.start(FIELD_SENDING_SCHOOL).is(sendingSchool).get());
        }

        final String sendingClass = applicationQueryParameters.getSendingClass();
        if (!isEmpty(sendingClass)) {
            filters.add(QueryBuilder.start().or(
                    QueryBuilder.start(FIELD_SENDING_CLASS).is(sendingClass.toUpperCase()).get(),
                    QueryBuilder.start(FIELD_CLASS_LEVEL).is(sendingClass.toUpperCase()).get()
            ).get());
        }

        final Date updatedAfter = applicationQueryParameters.getUpdatedAfter();
        if (updatedAfter != null) {
            filters.add(
                    QueryBuilder.start().or(
                            QueryBuilder.start(FIELD_RECEIVED).greaterThanEquals(updatedAfter.getTime()).get(),
                            QueryBuilder.start(FIELD_UPDATED).greaterThanEquals(updatedAfter.getTime()).get()
                    ).get()
            );
        }

        final String kohdejoukko = filterParameters.getKohdejoukko();
        final Set<String> baseEducation = applicationQueryParameters.getBaseEducation();
        if (isNotBlank(kohdejoukko) && !baseEducation.isEmpty()) {
            if (OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(kohdejoukko)) { // TODO: tää on yllättävää

                List<DBObject> ors = new LinkedList<>();

                for (String education : baseEducation) {
                    if (isNotBlank(education)) {
                        ors.add(
                                QueryBuilder.start(format(FIELD_HIGHER_ED_BASE_ED_T, education))
                                        .is(Boolean.TRUE.toString()).get()
                        );
                    }
                }

                if (!ors.isEmpty()) {
                    filters.add(
                            QueryBuilder.start().or(ors.toArray(new DBObject[ors.size()])).get()
                    );
                }
            }
        }

        final List<String> personOids = applicationQueryParameters.getPersonOids();
        if (!personOids.isEmpty()) {
            filters.add(QueryBuilder.start(FIELD_PERSON_OID).in(personOids).get());
        }

        return filters;
    }
    
    private DBObject aoOidElemMatcher(List<String> aoOids, QueryBuilder query, String aoFieldName) {
        if (aoOids.isEmpty()) {
            return query.get();
        } else {
            return query.and(aoFieldName).in(aoOids).get();
        }
    }

    private DBObject _createPreferenceFilters(final ApplicationQueryParameters applicationQueryParameters, final ApplicationFilterParameters filterParameters) {
        final List<String> aoOids = applicationQueryParameters.getAoOids();
        final String lopOid = applicationQueryParameters.getLopOid();
        final String preference = applicationQueryParameters.getAoId();
        final String groupOid = applicationQueryParameters.getGroupOid();
        boolean discretionaryOnly = applicationQueryParameters.isDiscretionaryOnly();
        boolean primaryPreferenceOnly = applicationQueryParameters.isPrimaryPreferenceOnly();

        if (isBlank(lopOid) && isBlank(preference) && isBlank(groupOid) && !discretionaryOnly && !primaryPreferenceOnly) {
            if (!aoOids.isEmpty()) {
                // Simple query: just find by application option oid
                return QueryBuilder.start(META_FIELD_AO).in(aoOids).get();
            }
            // No query parameters related to application options
            return null;
        }

        int maxOptions = primaryPreferenceOnly && (isBlank(groupOid) || aoOids.isEmpty())
                ? 1
                : filterParameters.getMaxApplicationOptions();

        final ArrayList<DBObject> preferenceQueries = new ArrayList<>();
        for (int i = 1; i <= maxOptions; i++) {
            ArrayList<DBObject> preferenceQuery = new ArrayList<>(filterParameters.getMaxApplicationOptions());
            if (isNotBlank(lopOid)) {
                preferenceQuery.add(
                        QueryBuilder.start(format(META_LOP_PARENTS_T, i)).in(Lists.newArrayList(lopOid)).get());
            }
            if (isNotBlank(preference)) {
                preferenceQuery.add(
                        QueryBuilder.start(format(FIELD_AO_KOULUTUS_ID_T, i)).is(preference).get());
            }
            if (discretionaryOnly) {
                preferenceQuery.add(
                        QueryBuilder.start(format(FIELD_DISCRETIONARY_T, i)).is("true").get());
            }
            if (!aoOids.isEmpty()) {
                preferenceQuery.add(
                        QueryBuilder.start(format(FIELD_AO_T, i)).in(aoOids).get());
            }
            if (isNotBlank(groupOid)) {
                if (!primaryPreferenceOnly) {
                    preferenceQuery.add(QueryBuilder.start(format(FIELD_AO_GROUPS_T, i)).regex(Pattern.compile(groupOid)).get());
                } else {
                    if (!aoOids.isEmpty()) {
                        // Hakukohteen pitää olla jokin annetuista, ja lisäksi olla hakemuksella valittuun ryhmään kuuluvista ensisijainen
                        for (int j = 1; j < i; j++) {
                            preferenceQuery.add(
                                QueryBuilder.start(format(FIELD_AO_GROUPS_T, j)).not().regex(Pattern.compile(groupOid)).get()
                            );
                        }
                        preferenceQuery.add(
                            QueryBuilder.start().and(
                                QueryBuilder.start(format(FIELD_AO_GROUPS_T, i)).regex(Pattern.compile(groupOid)).get(),
                                QueryBuilder.start(format(FIELD_AO_T, i)).in(aoOids).get()
                            ).get()
                        );
                    } else {
                        // Hakukohteen pitää olla ensisijainen ja kuulua valittuun ryhmään
                        preferenceQuery.add(QueryBuilder.start(format(FIELD_AO_GROUPS_T, i)).regex(Pattern.compile(groupOid)).get());
                    }
                }
            }

            if (!preferenceQuery.isEmpty()) {
                preferenceQueries.add(QueryBuilder.start().and(
                        preferenceQuery.toArray(new DBObject[preferenceQuery.size()])).get());
            } else {
                System.out.println();
            }
        }

        return _combineQueries(OPERATOR_OR, preferenceQueries);
    }

    private boolean skipOrganizationFilter(final ApplicationFilterParameters filterParameters) {
        return OppijaConstants.HAKUTAPA_YHTEISHAKU.equals(filterParameters.getHakutapa())
                && StringUtils.isBlank(filterParameters.getOrganizationFilter())
                && OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(filterParameters.getKohdejoukko())
                && !filterParameters.getOrganizationsHetuttomienKasittely().isEmpty();
    }

    private DBObject _filterByOrganization(final ApplicationFilterParameters filterParameters) {
        if (skipOrganizationFilter(filterParameters)) {
            return QueryBuilder.start(FIELD_APPLICATION_OID).exists(true).get();
        }

        final ArrayList<DBObject> queries = new ArrayList<>();

        final ArrayList<String> allowedOrganizations = new ArrayList<>();

        if (!StringUtils.isBlank(filterParameters.getOrganizationFilter())) {
            return QueryBuilder.start(META_ALL_ORGANIZATIONS).is(filterParameters.getOrganizationFilter()).get();
        }

        if (filterParameters.getOrganizationsReadble().size() > 0) {
            allowedOrganizations.addAll(filterParameters.getOrganizationsReadble());
        }

        if (filterParameters.getOrganizationsReadble().contains(rootOrganizationOid)) {
            allowedOrganizations.add(null);
        }

        if (allowedOrganizations.size() > 0) {
            queries.add(QueryBuilder.start(META_ALL_ORGANIZATIONS).in(allowedOrganizations).get());
        }

        if (filterParameters.getOrganizationsOpo().size() > 0) {
            queries.add(QueryBuilder.start().and(
                    QueryBuilder.start(META_SENDING_SCHOOL_PARENTS).in(filterParameters.getOrganizationsOpo()).get(),
                    QueryBuilder.start(META_FIELD_OPO_ALLOWED).is(true).get()).get());
        }


        LOG.debug("queries: {}", queries.size());

        final DBObject query = _combineQueries(OPERATOR_OR, queries);
        return query;
    }

    private DBObject _combineQueries(final String operator, final List<DBObject> queries){
        if (queries.size() < 1)
            return null;
        else if (queries.size() > 1)
            return new BasicDBObject(operator, queries);
        return queries.get(0);
    }
}
