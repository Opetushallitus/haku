package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

import com.google.api.client.util.Key;
import com.google.api.client.util.Throwables;
import com.google.api.client.util.Value;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.*;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import fi.vm.sade.haku.http.HttpRestClient.Response;
import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.haku.oppija.hakemus.service.EducationRequirementsUtil;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.PaymentEmail;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOptionOid;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.IsoCountryCode;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.SafeString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final SafeString koulutusinformaatioUrl;
    private SafeString koodistoServiceUrl;

    public HakumaksuUtil(RestClient restClient, SafeString koulutusinformaatioUrl, SafeString koodistoServiceUrl) {
        this.restClient = restClient;
        this.koulutusinformaatioUrl = koulutusinformaatioUrl;
        this.koodistoServiceUrl = koodistoServiceUrl;
        populateEaaCountriesCache();
    }

    public enum LanguageCodeISO6391 {
        @Value fi,
        @Value sv,
        @Value en
    }

    public enum CacheKeys {
        EAA_COUNTRIES
    }

    public static final String NUMERIC_COUNTRY_KOODISTO = "maatjavaltiot2";
    public static final String ISO_COUNTRY_KOODISTO = "maatjavaltiot1";
    public static final String EEA_KOODI = "valtioryhmat_2";
    public static final IsoCountryCode SVEITSI = IsoCountryCode.of("CHE");

    public static class OppijanTunnistus {
        public static class Metadata {
            @Key
            public String hakemusOid;
            @Key
            public String personOid;
        }
        @Key
        public String subject; // Email subject

        @Key
        public String template; // Email body template

        @Key
        public String url;

        @Key
        public long expires; // Url expiration time in Unix milliseconds

        @Key
        public String email;

        @Key
        public LanguageCodeISO6391 lang;

        @Key
        public Metadata metadata;
    }

    /**
     * @return true if send was successful
     */
    public ListenableFuture<Boolean> sendPaymentRequest(final PaymentEmail paymentEmail,
                                                        final SafeString oppijanTunnistusUrl,
                                                        final SafeString redirectUrl,
                                                        final ApplicationOid _hakemusOid,
                                                        final PersonOid _personOid,
                                                        final SafeString emailAddress) {
        OppijanTunnistus body = new OppijanTunnistus() {{
            this.url = redirectUrl.getValue();
            this.expires = paymentEmail.expirationDate.getTime();
            this.email = emailAddress.getValue();
            this.subject = paymentEmail.subject.getValue();
            this.template = paymentEmail.template.getValue();
            this.lang = paymentEmail.language;
            this.metadata = new Metadata() {{
                this.hakemusOid = _hakemusOid.getValue();
                this.personOid = _personOid.getValue();
            }};
        }};
        try {
            return Futures.transform(restClient.post(oppijanTunnistusUrl.getValue(), body, Object.class), new Function<Response<Object>, Boolean>() {
                @Override
                public Boolean apply(Response<Object> input) {
                    return input.isSuccessStatusCode();
                }
            });
        } catch (IOException e) {
            LOGGER.error("Error connecting oppijan-tunnistus for hakemusOid " + _hakemusOid + ", personOid " + _personOid + ", emailAddress " + emailAddress, e);
            return Futures.immediateFuture(false);
        }
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
                        LOGGER.error(msg);
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
        String url = koodistoServiceUrl + "/rest/codeelement/" + EEA_KOODI + "/1";
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
        String url = koodistoServiceUrl + "/rest/codeelement/" + NUMERIC_COUNTRY_KOODISTO + "_" + numericCode + "/1";
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
                    String url = String.format("%s/%s", koulutusinformaatioUrl.getValue(), applicationOptionOid.getValue());
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
}
