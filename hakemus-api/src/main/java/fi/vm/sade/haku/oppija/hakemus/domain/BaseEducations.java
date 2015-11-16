package fi.vm.sade.haku.oppija.hakemus.domain;

import com.google.common.collect.ImmutableSet;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.AsciiCountryCode;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class BaseEducations {
    private static boolean variablesNotNull(Object... vars) {
        int nullsFound = 0;
        for (Object v : vars) {
            if (v == null) {
                nullsFound++;
            }
        }
        if (vars.length == nullsFound) {
            return false;
        } else if (nullsFound == 0) {
            return true;
        } else {
            // Thrown if fields partially null
            throw new AssertionError("Not-nullable class got null arguments. Arguments are: " + Arrays.toString(vars));
        }
    }

    public static class UlkomaalainenKoulutus {
        public final AsciiCountryCode maa;
        public final String nimike;

        private UlkomaalainenKoulutus(AsciiCountryCode maa, String nimike) {
            this.maa = maa;
            this.nimike = nimike;
        }

        public static Set<UlkomaalainenKoulutus> of(Application application) {
            Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
            String multipleChoiceField = "pohjakoulutus_ulk";
            String valinta = baseEducation.get(multipleChoiceField);

            ImmutableSet.Builder<UlkomaalainenKoulutus> result = ImmutableSet.builder();

            if ("true".equals(valinta)) {
                for (int i = 1; ; i++) {
                    String index = (i == 1) ? "" : Integer.toString(i);
                    String nimike = baseEducation.get(multipleChoiceField + "_nimike" + index);
                    String maa = baseEducation.get(multipleChoiceField + "_suoritusmaa" + index);

                    if (variablesNotNull(nimike, maa)) {
                        result.add(new UlkomaalainenKoulutus(AsciiCountryCode.of(maa), nimike));
                    } else {
                        break;
                    }
                }
            }

            return result.build();
        }
    }
}
