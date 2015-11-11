package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

import com.google.api.client.util.Key;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import fi.vm.sade.haku.http.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class HakumaksuUtil {
    public static class CodeElement {
        @Key
        String codeElementValue;
        @Key
        String codeElementUri;
    }

    public static class KoodistoEAA {
        @Key
        public List<CodeElement> withinCodeElements;
    }

    private static final Predicate<CodeElement> maatJaValtiot2 = new Predicate<CodeElement>() {
        @Override
        public boolean apply(CodeElement input) {
            return input.codeElementUri.equals("maatjavaltiot2_" + input.codeElementValue);
        }
    };

    private static ListenableFuture<List<String>> getEaaCountryCodes() throws IOException, ExecutionException, InterruptedException {
        String url = "https://testi.virkailija.opintopolku.fi/koodisto-service/rest/codeelement/valtioryhmat_2/1";
        return Futures.transform(RestClient.get(url, KoodistoEAA.class), new Function<KoodistoEAA, List<String>>() {
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
        List<CodeElement> levelsWithCodeElements;
    }

    private static ListenableFuture<String> asciiToNumericCountryCode(String threeLetterCountryCode) throws IOException {
        String url = "https://testi.virkailija.opintopolku.fi/koodisto-service/rest/codeelement/maatjavaltiot1_" + threeLetterCountryCode.toLowerCase() + "/1";
        return Futures.transform(RestClient.get(url, KoodistoMaakoodi.class), new Function<KoodistoMaakoodi, String>() {
            @Override
            public String apply(KoodistoMaakoodi input) {
                CodeElement codeElement = Iterables.find(input.levelsWithCodeElements, maatJaValtiot2);
                return codeElement.codeElementValue;
            }
        });
    }

    private static boolean isSwitzerland(String threeLetterCountryCode) {
        return threeLetterCountryCode.equals("CHE");
    }

    private static Boolean _isExemptFromPayment(String threeLetterCountryCode) {
        try {
            return isSwitzerland(threeLetterCountryCode) ||
                    getEaaCountryCodes().get().contains(asciiToNumericCountryCode(threeLetterCountryCode).get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Country code " + threeLetterCountryCode + " not found", e);
        }
    }

    private static final LoadingCache<String, Boolean> exemptions = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Boolean>() {
                public Boolean load(String threeLetterCountryCode) {
                    return _isExemptFromPayment(threeLetterCountryCode);
                }
            });

    public static boolean isExemptFromPayment(String threeLetterCountryCode) throws InterruptedException, ExecutionException, IOException {
        return exemptions.get(threeLetterCountryCode);
    }

    public static void main(String[] args) {
        try {
            String countryCode = "FIN";
            System.out.println(countryCode + " is in EAA: " + isExemptFromPayment(countryCode));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
