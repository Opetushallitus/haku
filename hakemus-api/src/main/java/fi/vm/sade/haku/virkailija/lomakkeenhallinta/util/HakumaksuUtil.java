package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

import com.google.api.client.util.Key;
import com.google.api.client.util.Value;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import fi.vm.sade.haku.http.HttpRestClient.Response;
import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.PaymentEmail;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOptionOid;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.AsciiCountryCode;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.Oid;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.SafeString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class HakumaksuUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(HakumaksuUtil.class);

    private RestClient restClient;
    private final SafeString koulutusinformaatioUrl;

    public HakumaksuUtil(RestClient restClient, SafeString koulutusinformaatioUrl) {
        this.restClient = restClient;
        this.koulutusinformaatioUrl = koulutusinformaatioUrl;
    }

    public enum LanguageCodeISO6391 {
        @Value fi,
        @Value sv,
        @Value en
    }

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
                                                        final Oid _hakemusOid,
                                                        final Oid _personOid,
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

    static private class HakumaksuQuery {
        final SafeString serviceUrl;
        final AsciiCountryCode countryCode;

        public HakumaksuQuery(SafeString serviceUrl, AsciiCountryCode countryCode) {
            this.serviceUrl = serviceUrl;
            this.countryCode = countryCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            HakumaksuQuery that = (HakumaksuQuery) o;

            if (serviceUrl != null ? !serviceUrl.equals(that.serviceUrl) : that.serviceUrl != null)
                return false;
            return !(countryCode != null ? !countryCode.equals(that.countryCode) : that.countryCode != null);

        }

        @Override
        public int hashCode() {
            int result = serviceUrl != null ? serviceUrl.hashCode() : 0;
            result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);
            return result;
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

    private final Predicate<CodeElement> maatJaValtiot2 = new Predicate<CodeElement>() {
        @Override
        public boolean apply(CodeElement input) {
            return input.codeElementUri.equals("maatjavaltiot2_" + input.codeElementValue);
        }
    };

    private ListenableFuture<List<String>> getEaaCountryCodes(HakumaksuQuery query) throws IOException {
        String url = query.serviceUrl + "/rest/codeelement/valtioryhmat_2/1";
        return Futures.transform(restClient.get(url, KoodistoEAA.class), new Function<Response<KoodistoEAA>, List<String>>() {
            @Override
            public List<String> apply(Response<KoodistoEAA> response) {
                Iterable<CodeElement> validatedCodeElements = Iterables.filter(response.getResult().withinCodeElements, maatJaValtiot2);
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

    private ListenableFuture<String> asciiToNumericCountryCode(HakumaksuQuery query) throws IOException {
        String url = query.serviceUrl + "/rest/codeelement/maatjavaltiot1_" + query.countryCode.getValue().toLowerCase() + "/1";
        return Futures.transform(restClient.get(url, KoodistoMaakoodi.class), new Function<Response<KoodistoMaakoodi>, String>() {
            @Override
            public String apply(Response<KoodistoMaakoodi> response) {
                CodeElement codeElement = Iterables.find(response.getResult().levelsWithCodeElements, maatJaValtiot2);
                return codeElement.codeElementValue;
            }
        });
    }

    public static final AsciiCountryCode SVEITSI = AsciiCountryCode.of("CHE");

    private boolean isSwitzerland(AsciiCountryCode countryCode) {
        return countryCode.equals(SVEITSI);
    }

    private Boolean _isExemptFromPayment(HakumaksuQuery query) {
        try {
            return isSwitzerland(query.countryCode) ||
                    getEaaCountryCodes(query).get().contains(asciiToNumericCountryCode(query).get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Country code " + query.countryCode + " not found", e);
        }
    }

    private final LoadingCache<HakumaksuQuery, Boolean> exemptions = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<HakumaksuQuery, Boolean>() {
                public Boolean load(HakumaksuQuery query) {
                    return _isExemptFromPayment(query);
                }
            });

    public boolean isEducationCountryExemptFromPayment(SafeString koodistoServiceUrl, AsciiCountryCode threeLetterCountryCode) throws ExecutionException {
        return exemptions.get(new HakumaksuQuery(koodistoServiceUrl, threeLetterCountryCode));
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
                    String url = koulutusinformaatioUrl.getValue() + "/" + applicationOptionOid.getValue();
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
                    throw new IllegalStateException("Getting education requirements for " + applicationOptionId, e);
                }
            }
        });
    }
}
