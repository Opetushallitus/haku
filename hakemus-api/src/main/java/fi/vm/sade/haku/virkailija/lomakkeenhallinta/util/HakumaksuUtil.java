package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

import com.google.api.client.util.Key;
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
import fi.vm.sade.haku.http.HttpRestClient;
import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.ApplicationOptionOid;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.AsciiCountryCode;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class HakumaksuUtil {

    private RestClient restClient;

    public HakumaksuUtil(RestClient restClient) {
        this.restClient = restClient;
    }

    static private class HakumaksuQuery {
        final String serviceUrl;
        final AsciiCountryCode countryCode;

        public HakumaksuQuery(String serviceUrl, AsciiCountryCode countryCode) {
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

    private ListenableFuture<List<String>> getEaaCountryCodes(HakumaksuQuery query) throws IOException, ExecutionException, InterruptedException {
        String url = query.serviceUrl + "/rest/codeelement/valtioryhmat_2/1";
        return Futures.transform(restClient.get(url, KoodistoEAA.class), new Function<KoodistoEAA, List<String>>() {
            @Override
            public List<String> apply(KoodistoEAA input) {
                Iterable<CodeElement> validatedCodeElements = Iterables.filter(input.withinCodeElements, maatJaValtiot2);
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
        return Futures.transform(restClient.get(url, KoodistoMaakoodi.class), new Function<KoodistoMaakoodi, String>() {
            @Override
            public String apply(KoodistoMaakoodi input) {
                CodeElement codeElement = Iterables.find(input.levelsWithCodeElements, maatJaValtiot2);
                return codeElement.codeElementValue;
            }
        });
    }

    private boolean isSwitzerland(AsciiCountryCode countryCode) {
        return countryCode.getValue().equals("CHE");
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

    public boolean isExemptFromPayment(String koodistoServiceUrl, AsciiCountryCode threeLetterCountryCode) throws ExecutionException {
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

    public Iterable<EducationRequirements> getEducationRequirements(final String koulutusinformaatioUrl,
                                                                           List<String> applicationOptions) {
        return Iterables.transform(applicationOptions, new Function<String, EducationRequirements>() {
            @Override
            public EducationRequirements apply(String applicationOptionId) {
                // TODO: Hae RESTillä hakutoiveiden vaatimukset (ja cacheta niitä)
                // Esimerkki: GET https://testi.opintopolku.fi/ao/1.2.246.562.20.40822369126
                //
                // requiredBaseEducations: [
                //   "pohjakoulutusvaatimuskorkeakoulut_123"
                // ]
                String url = koulutusinformaatioUrl + "/" + applicationOptionId;
                try {
                    System.err.println(url);
                    BaseEducationRequirements requirements = restClient.get(url, BaseEducationRequirements.class).get();
                    return new EducationRequirements(ApplicationOptionOid.of(applicationOptionId), ImmutableSet.copyOf(requirements.requiredBaseEducations));
                } catch (ExecutionException|InterruptedException|IOException e) {
                    // TODO: what to do?
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }
}
