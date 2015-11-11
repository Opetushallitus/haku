package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

import com.google.api.client.util.Key;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import fi.vm.sade.haku.http.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

    public static ListenableFuture<List<String>> getEaaCountryCodes() throws IOException, ExecutionException, InterruptedException {
        String url = "https://testi.virkailija.opintopolku.fi/koodisto-service/rest/codeelement/valtioryhmat_2/1";
        return Futures.transform(RestClient.get(url, KoodistoEAA.class), new Function<KoodistoEAA, List<String>>() {
            @Override
            public List<String> apply(KoodistoEAA input) {
                return Lists.transform(input.withinCodeElements, new Function<CodeElement, String>() {
                    @Override
                    public String apply(CodeElement input) {
                        return input.codeElementValue;
                    }
                });
            }
        });
    }

    public static class KoodistoMaakoodi {
        @Key
        List<CodeElement> levelsWithCodeElements;
    }

    public static ListenableFuture<String> asciiToNumericCountryCode(String threeLetterCountryCode) throws IOException {
        String url = "https://testi.virkailija.opintopolku.fi/koodisto-service/rest/codeelement/maatjavaltiot1_" + threeLetterCountryCode.toLowerCase() + "/1";
        return Futures.transform(RestClient.get(url, KoodistoMaakoodi.class), new Function<KoodistoMaakoodi, String>() {
            @Override
            public String apply(KoodistoMaakoodi input) {
                CodeElement codeElement = Iterables.find(input.levelsWithCodeElements, new Predicate<CodeElement>() {
                    @Override
                    public boolean apply(CodeElement input) {
                        return input.codeElementUri.equals("maatjavaltiot2_" + input.codeElementValue);
                    }
                });
                return codeElement.codeElementValue;
            }
        });
    }

    public static void main(String[] args) {
        try {
            ListenableFuture<List<String>> eaaCountryCodes = HakumaksuUtil.getEaaCountryCodes();
            String asciiCountryCode = "FIN";
            String numericCountryCode = asciiToNumericCountryCode(asciiCountryCode).get();
            boolean isEaa = eaaCountryCodes.get().contains(numericCountryCode);
            System.out.println(asciiCountryCode + " is in EAA: " + isEaa);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
