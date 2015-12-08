package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

import com.google.api.client.util.Key;
import com.google.api.client.util.Value;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import fi.vm.sade.haku.http.HttpRestClient.Response;
import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.haku.oppija.hakemus.service.EducationRequirementsUtil;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.PaymentEmail;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOptionOid;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.AsciiCountryCode;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.Oid;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.SafeString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class HakumaksuUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(HakumaksuUtil.class);

    private RestClient restClient;
    private final SafeString koulutusinformaatioUrl;
    private SafeString koodistoServiceUrl;

    public HakumaksuUtil(RestClient restClient, SafeString koulutusinformaatioUrl, SafeString koodistoServiceUrl) {
        this.restClient = restClient;
        this.koulutusinformaatioUrl = koulutusinformaatioUrl;
        this.koodistoServiceUrl = koodistoServiceUrl;
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

    private ListenableFuture<List<String>> getEaaCountryCodes() throws IOException {
        String url = koodistoServiceUrl + "/rest/codeelement/valtioryhmat_2/1";
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

    private ListenableFuture<String> asciiToNumericCountryCode(AsciiCountryCode countryCode) throws IOException {
        String url = koodistoServiceUrl + "/rest/codeelement/maatjavaltiot1_" + countryCode.getValue().toLowerCase() + "/1";
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

    private Boolean _isExemptFromPayment(AsciiCountryCode countryCode) {
        try {
            return isSwitzerland(countryCode) ||
                    getEaaCountryCodes().get().contains(asciiToNumericCountryCode(countryCode).get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Country code " + countryCode + " not found", e);
        }
    }

    private final LoadingCache<AsciiCountryCode, Boolean> exemptions = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<AsciiCountryCode, Boolean>() {
                public Boolean load(AsciiCountryCode countryCode) {
                    return _isExemptFromPayment(countryCode);
                }
            });

    public boolean isEducationCountryExemptFromPayment(AsciiCountryCode threeLetterCountryCode) throws ExecutionException {
        return exemptions.get(threeLetterCountryCode);
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

    public static ImmutableMap<String, String> paymentNotificationAnswers(Map<String, String> answers, ImmutableMap<ApplicationOptionOid, ImmutableSet<EducationRequirementsUtil.Eligibility>> paymentRequirements) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (String key: answers.keySet()){
            if (key != null && key.startsWith(PREFERENCE_PREFIX) && key.endsWith(OPTION_ID_POSTFIX) && isNotEmpty(answers.get(key))){
                ImmutableSet<EducationRequirementsUtil.Eligibility> eligibilities = paymentRequirements.get(ApplicationOptionOid.of(answers.get(key)));
                if (!eligibilities.isEmpty()) {
                    String preferenceString = key.replace(OPTION_ID_POSTFIX, "");
                    String paymentRequirementKey = preferenceString + PAYMENT_NOTIFICATION_POSTFIX;
                    builder.put(paymentRequirementKey, "true");
                    builder.put(PHASE_EDUCATION + PAYMENT_NOTIFICATION_POSTFIX, "true");
                }

            }
        }
        return builder.build();
    }
}
