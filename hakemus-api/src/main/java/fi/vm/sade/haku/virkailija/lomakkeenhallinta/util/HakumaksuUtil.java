package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Charsets;
import com.google.api.client.util.Key;
import com.google.api.client.util.Throwables;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import fi.vm.sade.haku.http.HttpRestClient.Response;
import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.haku.oppija.common.oppijantunnistus.OppijanTunnistusDTO;
import fi.vm.sade.haku.oppija.configuration.HakemusApiCallerId;
import fi.vm.sade.haku.oppija.hakemus.service.EducationRequirementsUtil;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.PaymentEmail;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOptionOid;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.IsoCountryCode;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.SafeString;
import fi.vm.sade.javautils.cas.CasClient;
import fi.vm.sade.properties.OphProperties;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOid;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.PersonOid;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class HakumaksuUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(HakumaksuUtil.class);
    public static final String TRUE = "true";

    private RestClient restClient;
    private final HttpClient oppijanTunnistusClient;
    private final OphProperties urlConfiguration;

    final String clientAppUser;
    final String clientAppPass;

    public HakumaksuUtil(RestClient restClient, OphProperties urlConfiguration,
                         HttpClient oppijanTunnistusClient,
                         final String clientAppUser,
                         final String clientAppPass) {
        this.restClient = restClient;
        this.urlConfiguration = urlConfiguration;
        this.oppijanTunnistusClient = oppijanTunnistusClient;
        this.clientAppUser= clientAppUser;
        this.clientAppPass= clientAppPass;
        populateEaaCountriesCache();
    }

    public enum CacheKeys {
        EAA_COUNTRIES
    }

    public static final String NUMERIC_COUNTRY_KOODISTO = "maatjavaltiot2";
    public static final String ISO_COUNTRY_KOODISTO = "maatjavaltiot1";
    public static final String EEA_KOODI = "valtioryhmat_2";
    public static final IsoCountryCode SVEITSI = IsoCountryCode.of("CHE");

    public static final int APPLICATION_PAYMENT_GRACE_PERIOD = 10;
    public static final long APPLICATION_PAYMENT_GRACE_PERIOD_MILLIS = TimeUnit.DAYS.toMillis(APPLICATION_PAYMENT_GRACE_PERIOD);

    public static final int APPLICATION_PAYMENT_WAITING_TIME = 2;
    public static final long APPLICATION_PAYMENT_WAITING_TIME_MILLIS = TimeUnit.DAYS.toMillis(APPLICATION_PAYMENT_WAITING_TIME);

    public volatile String OPPIJAN_TUNNISTUS_SESSION = "InvalidSession";

    public synchronized void updateOppijanTunnistusSession(String currentSession) {
        if(OPPIJAN_TUNNISTUS_SESSION.equals(currentSession)) {
            String ticket = getTicket();
            Header session = getSession(oppijanTunnistusClient, ticket)[0];
            OPPIJAN_TUNNISTUS_SESSION = session.getElements()[0].getValue();
        }
    }
    public String toJson(OppijanTunnistusDTO body) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            JsonGenerator generator = new JacksonFactory().createJsonGenerator(out, Charsets.UTF_8);
            generator.serialize(body);
            generator.flush();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new String(out.toByteArray());
    }

    public HttpPost postRequest(String uri, String session, String body) {
        HttpPost req2 = new HttpPost(uri);

        req2.setHeader("Caller-Id", HakemusApiCallerId.callerId);
        req2.setHeader("CSRF", "HttpRestClient");
        req2.setHeader("Cookie", "CSRF=HttpRestClient");
        req2.setHeader("Content-Type","application/json; charset=utf-8");
        req2.setHeader("Cookie","ring-session="+session);
        req2.setEntity(EntityBuilder.create().setText(body).build());
        return req2;
    }

    public Integer makeOppijanTunnistusCallWithBody(OppijanTunnistusDTO json) {
        String body = toJson(json);
        final java.util.function.Function<String, Integer> callOppijanTunnistus = (session) -> {
            try {
                return oppijanTunnistusClient.execute(
                    postRequest(
                        urlConfiguration.url("oppijan-tunnistus.create"),
                        session,
                        body)).getStatusLine().getStatusCode();
            } catch(Exception e) {
                LOGGER.error("Error connecting oppijan-tunnistus for hakemusOid: " + body, e);
                throw new RuntimeException(e);
            }
        };

        final String session = OPPIJAN_TUNNISTUS_SESSION;
        Integer statusCode = callOppijanTunnistus.apply(session);
        if(statusCode.equals(401)) {
            updateOppijanTunnistusSession(session);
            return callOppijanTunnistus.apply(OPPIJAN_TUNNISTUS_SESSION);
        } else {
            return statusCode;
        }
    }

    /**
     * @return true if send was successful
     */
    public Integer sendPaymentRequest(final PaymentEmail paymentEmail,
                                        final String redirectUrl,
                                        final ApplicationOid _hakemusOid,
                                        final PersonOid _personOid,
                                        final SafeString emailAddress) {
        return makeOppijanTunnistusCallWithBody(new OppijanTunnistusDTO() {{
            this.url = redirectUrl;
            this.expires = paymentEmail.expirationDate.getTime();
            this.email = emailAddress.getValue();
            this.subject = paymentEmail.subject.getValue();
            this.template = paymentEmail.template.getValue();
            this.lang = paymentEmail.language;
            this.metadata = new Metadata() {{
                this.hakemusOid = _hakemusOid.getValue();
                this.personOid = _personOid.getValue();
            }};
        }});
    }

    public static class CodeElement {
        @Key
        public String codeElementValue;
        @Key
        public String codeElementUri;
    }

    public static class KoodistoEAA {
        @Key
        public List<CodeElement> withinCodeElements;
    }

    private final LoadingCache<CacheKeys, ImmutableSet<IsoCountryCode>> koodistoCache = CacheBuilder.newBuilder()
            .maximumSize(1) // There is only one value to cache
            .build(new CacheLoader<CacheKeys, ImmutableSet<IsoCountryCode>>() {
                public ImmutableSet<IsoCountryCode> load(CacheKeys cacheKey) {
                    try {
                        return getIsoEaaCountryCodes();
                    } catch (IOException e) {
                        String msg = "Koodisto fetch failed: " + e.toString();
                        LOGGER.error(msg);
                        throw new RuntimeException(msg);
                    } catch (InterruptedException | ExecutionException e) {
                        String msg = "Koodisto cache failed to load: " + e.toString();
                        LOGGER.error(msg, e);
                        throw new RuntimeException(msg);
                    }
                }
            });

    private void populateEaaCountriesCache() {
        Set<IsoCountryCode> eeaCountries = koodistoCache.getUnchecked(CacheKeys.EAA_COUNTRIES);
        if (eeaCountries.isEmpty()) {
            String msg = "Koodisto cache failed: EAA countries cannot be empty!";
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }
    }

    private static Predicate<CodeElement> filterByKoodisto(final String koodisto) {
        return new Predicate<CodeElement>() {
            @Override
            public boolean apply(CodeElement input) {
                return input.codeElementUri.startsWith(koodisto + "_");
            }
        };
    }

    private ImmutableSet<IsoCountryCode> getIsoEaaCountryCodes() throws IOException, InterruptedException, ExecutionException {
        List<IsoCountryCode> isoCountries = Futures.transform(getNumericEaaCountryCodes(), new AsyncFunction<List<String>, List<IsoCountryCode>>() {
            @Override
            public ListenableFuture<List<IsoCountryCode>> apply(List<String> numericCodes) {
                return Futures.allAsList(Iterables.transform(numericCodes, new Function<String, ListenableFuture<IsoCountryCode>>() {
                    @Override
                    public ListenableFuture<IsoCountryCode> apply(String numericCode) {
                        try {
                            return numericCountryCodeToIsoCountryCode(numericCode);
                        } catch (Throwable t) {
                            throw Throwables.propagate(t);
                        }
                    }
                }));
            }
        }).get();
        return ImmutableSet.copyOf(isoCountries);
    }

    private ListenableFuture<List<String>> getNumericEaaCountryCodes() throws IOException {
        String url = urlConfiguration.url("koodisto-service.koodi", EEA_KOODI);
        return Futures.transform(restClient.get(url, KoodistoEAA.class), new Function<Response<KoodistoEAA>, List<String>>() {
            @Override
            public List<String> apply(Response<KoodistoEAA> response) {
                Iterable<CodeElement> validatedCodeElements = Iterables.filter(response.getResult().withinCodeElements, filterByKoodisto(NUMERIC_COUNTRY_KOODISTO));
                return Lists.newArrayList(Iterables.transform(validatedCodeElements, new Function<CodeElement, String>() {
                    @Override
                    public String apply(CodeElement input) {
                        return input.codeElementValue;
                    }
                }));
            }
        });
    }

    public static class KoodistoMaakoodi {
        @Key
        public List<CodeElement> levelsWithCodeElements;
    }

    private ListenableFuture<IsoCountryCode> numericCountryCodeToIsoCountryCode(String numericCode) throws IOException {
        String url = urlConfiguration .url("koodisto-service.koodi", NUMERIC_COUNTRY_KOODISTO + "_" + numericCode);
        return Futures.transform(restClient.get(url, KoodistoMaakoodi.class), new Function<Response<KoodistoMaakoodi>, IsoCountryCode>() {
            @Override
            public IsoCountryCode apply(Response<KoodistoMaakoodi> response) {
                CodeElement codeElement = Iterables.find(response.getResult().levelsWithCodeElements, filterByKoodisto(ISO_COUNTRY_KOODISTO));
                return IsoCountryCode.of(codeElement.codeElementValue);
            }
        });
    }

    private boolean isSwitzerland(IsoCountryCode countryCode) {
        return countryCode.equals(SVEITSI);
    }

    public boolean isEducationCountryExemptFromPayment(IsoCountryCode isoCountryCode) throws ExecutionException {
        return isSwitzerland(isoCountryCode) || koodistoCache.get(CacheKeys.EAA_COUNTRIES).contains(isoCountryCode);
    }

    public static class EducationRequirements {
        public final ApplicationOptionOid applicationOptionId;
        public final ImmutableSet<String> baseEducationRequirements;

        public EducationRequirements(ApplicationOptionOid applicationOptionId, ImmutableSet<String> requiredBaseEducations) {
            this.applicationOptionId = applicationOptionId;
            this.baseEducationRequirements = requiredBaseEducations;
        }
    }

    public static class BaseEducationRequirements {
        @Key
        public List<String> requiredBaseEducations;
    }

    private final LoadingCache<ApplicationOptionOid, EducationRequirements> koulutusinformaatioBaseEducationRequirementsCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<ApplicationOptionOid, EducationRequirements>() {
                public EducationRequirements load(ApplicationOptionOid applicationOptionOid) throws Exception {
                    String url = urlConfiguration.url("koulutusinformaatio-app.ao",applicationOptionOid.getValue());
                    BaseEducationRequirements requirements = restClient.get(url, BaseEducationRequirements.class).get().getResult();
                    return new EducationRequirements(applicationOptionOid, ImmutableSet.copyOf(requirements.requiredBaseEducations));
                }
            });

    public Iterable<EducationRequirements> getEducationRequirements(final List<ApplicationOptionOid> applicationOptions) {
        return Iterables.transform(applicationOptions, new Function<ApplicationOptionOid, EducationRequirements>() {
            @Override
            public EducationRequirements apply(ApplicationOptionOid applicationOptionId) {
                try {
                    return koulutusinformaatioBaseEducationRequirementsCache.get(applicationOptionId);
                } catch (ExecutionException e) {
                    throw new IllegalStateException(String.format("Getting education requirements for %s", applicationOptionId), e);
                }
            }
        });
    }

    public static ImmutableMap<String, String> paymentNotificationAnswers(final Map<String, String> answers,
                                                                          final ImmutableMap<ApplicationOptionOid, ImmutableSet<EducationRequirementsUtil.Eligibility>> paymentRequirements) {

        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        for (String key: answers.keySet()) {
            if (key != null && key.startsWith(PREFERENCE_PREFIX) && key.endsWith(OPTION_ID_POSTFIX) && isNotEmpty(answers.get(key))){
                ImmutableSet<EducationRequirementsUtil.Eligibility> eligibilities = paymentRequirements.get(ApplicationOptionOid.of(answers.get(key)));
                if (!eligibilities.isEmpty()) {
                    builder.put(String.format("%s%s", key.replace(OPTION_ID_POSTFIX, EMPTY), PAYMENT_NOTIFICATION_POSTFIX), TRUE);
                }
            }
        }

        final ImmutableMap<String, String> paymentAnswers = builder.build();

        if (!paymentAnswers.isEmpty())  {
            builder.put(String.format("%s%s", PHASE_EDUCATION, PAYMENT_NOTIFICATION_POSTFIX), TRUE);

            return builder.build();
        } else {
            return paymentAnswers;
        }
    }
    public String getTicket() {
        return CasClient.getTicket(
            urlConfiguration.url("cas.url") + "/v1/tickets",
            clientAppUser,
            clientAppPass,
            urlConfiguration.url("oppijan-tunnistus.auth"),
            false);
    }
    public Header[] getSession(HttpClient httpClient, String ticket) {
        HttpGet req2 = new HttpGet(urlConfiguration.url("oppijan-tunnistus.cas",ticket));
        req2.setHeader("Caller-Id", HakemusApiCallerId.callerId);
        req2.setHeader("CSRF", "HttpRestClient");
        req2.setHeader("Cookie", "CSRF=HttpRestClient");
        try {

            HttpResponse t = httpClient.execute(req2);
            if (t.getStatusLine().getStatusCode() == 302) {
                return t.getHeaders("Set-Cookie");
            } else {
                throw new RuntimeException(String.format("CAS ticket fetch failed with statuscode %s:", t.getStatusLine().getStatusCode()));
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            req2.releaseConnection();
        }
    }

}
