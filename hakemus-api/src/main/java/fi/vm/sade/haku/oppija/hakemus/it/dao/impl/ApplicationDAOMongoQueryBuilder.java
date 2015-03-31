package fi.vm.sade.haku.oppija.hakemus.it.dao.impl;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationFilterParameters;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.haku.oppija.lomake.service.EncrypterService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
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
        final DBObject orgFilter = filterByOrganization(filterParameters);
        return QueryBuilder.start().and(
                new BasicDBObject(META_FIELD_AO, aoId),
                new BasicDBObject(FIELD_APPLICATION_SYSTEM_ID, applicationSystemId),
                QueryBuilder.start(FIELD_APPLICATION_STATE).in(
                        Lists.newArrayList(
                                Application.State.ACTIVE.toString(),
                                Application.State.INCOMPLETE.toString()))
                        .get(),
                orgFilter).get();
    }

    public DBObject buildApplicationExistsForSSN(final String ssn, final String asId) {
        final String encryptedSsn = shaEncrypter.encrypt(ssn.toUpperCase());
        return QueryBuilder.start(FIELD_APPLICATION_SYSTEM_ID).is(asId)
                .and("answers.henkilotiedot." + SocialSecurityNumber.HENKILOTUNNUS_HASH).is(encryptedSsn)
                .and(FIELD_APPLICATION_STATE).notEquals(State.PASSIVE)
                .get();
    }

    public DBObject buildApplicationExistsForSSN(final String ssn, final String asId, final String aoId) {
        final String encryptedSsn = shaEncrypter.encrypt(ssn.toUpperCase());
        return QueryBuilder.start(FIELD_APPLICATION_SYSTEM_ID).is(asId)
                .and("answers.henkilotiedot." + SocialSecurityNumber.HENKILOTUNNUS_HASH).is(encryptedSsn)
                .and(FIELD_APPLICATION_STATE).notEquals(State.PASSIVE)
                .and(META_FIELD_AO).is(aoId)
                .get();
    }

    public DBObject buildFindAllQuery(ApplicationQueryParameters applicationQueryParameters,
                                      ApplicationFilterParameters filterParameters) {
        LOG.debug("Entering buildQuery");

        final DBObject query = combineSearchTermAndFilterQueries(
                buildQueryFilter(applicationQueryParameters, filterParameters),
                filterByOrganization(filterParameters),
                createSearchTermQuery(applicationQueryParameters));
        LOG.debug("Constructed query: {}", query.toString());
        return query;
    }

    private DBObject combineSearchTermAndFilterQueries(final DBObject[] filters,
                                                       final DBObject orgFilter,
                                                       final DBObject searchTermQuery) {
        LOG.debug("Filters: {}", filters.length);
        if (orgFilter.keySet().isEmpty()) {
            return QueryBuilder.start("_id").is(null).get();
        }

        final ArrayList<DBObject> queries = new ArrayList<>(3 + filters.length);
        // doing tricks to retain old order. Feel free to refactor later
        if (null != searchTermQuery)
            queries.add(searchTermQuery);
        if (filters.length > 0) {
            queries.addAll(Lists.newArrayList(filters));
        }
        queries.add(orgFilter);

        return QueryBuilder.start().and(queries.toArray(new DBObject[queries.size()])).get();
    }

    private final DBObject createSearchTermQuery(final ApplicationQueryParameters applicationQueryParameters) {
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
                        queries.add(createDobOrNameQuery(searchTerm));
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
                queries.add(createDobOrNameQuery(searchTerm));
            }
        }

        if (queries.size() < 1)
            return null;
        else if (queries.size() > 1)
            return QueryBuilder.start().and(queries.toArray(new DBObject[queries.size()])).get();
        return queries.get(0);
    }

    private DBObject createDobOrNameQuery(final String searchTerm) {
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

    private DBObject[] buildQueryFilter(final ApplicationQueryParameters applicationQueryParameters,
                                        final ApplicationFilterParameters filterParameters) {
        final ArrayList<DBObject> filters = new ArrayList<>();
        final ArrayList<DBObject> preferenceQueries = createPreferenceFilters(applicationQueryParameters, filterParameters);

        if (!preferenceQueries.isEmpty()) {
            if (preferenceQueries.size() == 1) {
                filters.add(preferenceQueries.get(0));
            } else {
                filters.add(QueryBuilder.start().or(
                        preferenceQueries.toArray(new DBObject[preferenceQueries.size()])).get());
            }
        }

        final Boolean preferenceChecked = applicationQueryParameters.getPreferenceChecked();
        if (preferenceChecked != null) {
            final String aoOid = applicationQueryParameters.getAoOid();
            if (isNotBlank(aoOid)) {
                filters.add(
                        QueryBuilder.start("preferencesChecked").elemMatch(
                                QueryBuilder.start().and(
                                        new BasicDBObject("preferenceAoOid", aoOid),
                                        new BasicDBObject("checked", preferenceChecked)
                                ).get()
                        ).get()
                );
            } else {
                filters.add(
                        QueryBuilder.start().and(
                                QueryBuilder.start("preferencesChecked").not().elemMatch(
                                        new BasicDBObject("checked", !preferenceChecked)).get(),
                                QueryBuilder.start("preferencesChecked").exists(true).get()
                        ).get()
                );
            }
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
                    filters.add(QueryBuilder.start(FIELD_APPLICATION_STATE).is(Application.State.valueOf(states.get(0))).get());
                }
            } else {
                filters.add(QueryBuilder.start(FIELD_APPLICATION_STATE).in(states).get());
            }
        }

        final List<String> asIds = applicationQueryParameters.getAsIds();
        if (!asIds.isEmpty()) {
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
        final String baseEducation = applicationQueryParameters.getBaseEducation();
        if (isNotBlank(kohdejoukko) && isNotBlank(baseEducation)) {
            if (OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(kohdejoukko)) {
                filters.add(
                        QueryBuilder.start(format(FIELD_HIGHER_ED_BASE_ED_T, baseEducation))
                                .is(Boolean.TRUE.toString()).get()
                );
            }
        }

        return filters.toArray(new DBObject[filters.size()]);
    }

    private ArrayList<DBObject> createPreferenceFilters(final ApplicationQueryParameters applicationQueryParameters, final ApplicationFilterParameters filterParameters) {
        // Koskee yksittäistä hakutoivetta
        final String aoOid = applicationQueryParameters.getAoOid();
        final String lopOid = applicationQueryParameters.getLopOid();
        final String preference = applicationQueryParameters.getAoId();
        final String groupOid = applicationQueryParameters.getGroupOid();
        boolean discretionaryOnly = applicationQueryParameters.isDiscretionaryOnly();
        boolean primaryPreferenceOnly = applicationQueryParameters.isPrimaryPreferenceOnly();

        // FIXME A dirty Quickfix
        if (isBlank(lopOid) && isBlank(preference) && isBlank(groupOid) && !discretionaryOnly)
            return quickfix(aoOid);

        int maxOptions = primaryPreferenceOnly && isBlank(groupOid)
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
            if (isNotBlank(aoOid)) {
                preferenceQuery.add(
                        QueryBuilder.start(format(FIELD_AO_T, i)).is(aoOid).get());
            }
            if (isNotBlank(groupOid)) {
                if (!primaryPreferenceOnly) {
                    preferenceQuery.add(
                            QueryBuilder.start(format(FIELD_AO_GROUPS_T, i))
                                    .regex(Pattern.compile(groupOid)).get());
                } else {
                    for (int j = 1; j < i; j++) {
                        preferenceQuery.add(
                                QueryBuilder.start(format(FIELD_AO_GROUPS_T, j)).not().regex(Pattern.compile(groupOid)).get()
                        );
                    }
                    preferenceQuery.add(
                            QueryBuilder.start().and(
                                    QueryBuilder.start(format(FIELD_AO_GROUPS_T, i)).regex(Pattern.compile(groupOid)).get(),
                                    QueryBuilder.start(format(FIELD_AO_T, i)).is(aoOid).get()
                            ).get()
                    );
                }
            }

            if (!preferenceQuery.isEmpty()) {
                preferenceQueries.add(QueryBuilder.start().and(
                        preferenceQuery.toArray(new DBObject[preferenceQuery.size()])).get());
            }
        }
        return preferenceQueries;
    }

    private ArrayList<DBObject> quickfix(final String aoOid) {
        if (isNotBlank(aoOid))
            return Lists.newArrayList((DBObject) new BasicDBObject(META_FIELD_AO, aoOid));
        return new ArrayList<>(0);
    }

    private DBObject filterByOrganization(final ApplicationFilterParameters filterParameters) {
        final ArrayList<String> allowedOrganizations = new ArrayList<>();

        if (filterParameters.getOrganizationsReadble().size() > 0) {
            allowedOrganizations.addAll(filterParameters.getOrganizationsReadble());
        }

        if (filterParameters.getOrganizationsReadble().contains(rootOrganizationOid)) {
            allowedOrganizations.add(null);
        }

        final ArrayList<DBObject> queries = new ArrayList<>();
        if (allowedOrganizations.size() > 0) {
            queries.add(QueryBuilder.start(META_ALL_ORGANIZATIONS).in(allowedOrganizations).get());
        }

        if (filterParameters.getOrganizationsOpo().size() > 0) {
            queries.add(QueryBuilder.start().and(
                    QueryBuilder.start(META_SENDING_SCHOOL_PARENTS).in(filterParameters.getOrganizationsOpo()).get(),
                    QueryBuilder.start(META_FIELD_OPO_ALLOWED).is(true).get()).get());
        }

        if (OppijaConstants.HAKUTAPA_YHTEISHAKU.equals(filterParameters.getHakutapa())
                && OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(filterParameters.getKohdejoukko())
                && !filterParameters.getOrganizationsHetuttomienKasittely().isEmpty()) {
            queries.add(QueryBuilder.start(FIELD_SSN).is(null).get());
        }

        LOG.debug("queries: {}", queries.size());

        return QueryBuilder.start().or(queries.toArray(new DBObject[queries.size()])).get();
    }
}
