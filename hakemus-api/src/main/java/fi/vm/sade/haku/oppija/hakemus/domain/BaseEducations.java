package fi.vm.sade.haku.oppija.hakemus.domain;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.AsciiCountryCode;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Typing of Applications' base education Map<String, String> to concrete
 * NON NULL types.
 */
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

    public static class SuomalainenAvoinKoulutus {
        private static final String fieldPrefix = "pohjakoulutus_avoin";

        public final String nimike;

        private SuomalainenAvoinKoulutus(String nimike) {
            this.nimike = nimike;
        }

        public static Optional<SuomalainenAvoinKoulutus> of(Application application) {
            Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
            String valinta = baseEducation.get(fieldPrefix);

            if ("true".equals(valinta)) {
                String nimike = baseEducation.get(fieldPrefix + "_kokonaisuus");
                variablesNotNull(nimike);
                return Optional.of(new SuomalainenAvoinKoulutus(nimike));
            } else {
                return Optional.absent();
            }
        }
    }

    // TODO: in reality a list
    public static class SuomalainenAmKoulutus {
        private static final String fieldPrefix = "pohjakoulutus_am";

        public final String nimike;

        private SuomalainenAmKoulutus(String nimike) {
            this.nimike = nimike;
        }

        public static Optional<SuomalainenAmKoulutus> of(Application application) {
            Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
            String valinta = baseEducation.get(fieldPrefix);

            if ("true".equals(valinta)) {
                String nimike = baseEducation.get(fieldPrefix + "_nimike");
                variablesNotNull(nimike);
                return Optional.of(new SuomalainenAmKoulutus(nimike));
            } else {
                return Optional.absent();
            }
        }
    }

    // TODO: in reality a list
    public static class SuomalainenAmtKoulutus {
        private static final String fieldPrefix = "pohjakoulutus_amt";

        public final String nimike;

        private SuomalainenAmtKoulutus(String nimike) {
            this.nimike = nimike;
        }

        public static Optional<SuomalainenAmtKoulutus> of(Application application) {
            Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
            String valinta = baseEducation.get(fieldPrefix);

            if ("true".equals(valinta)) {
                String nimike = baseEducation.get(fieldPrefix + "_nimike");
                variablesNotNull(nimike);
                return Optional.of(new SuomalainenAmtKoulutus(nimike));
            } else {
                return Optional.absent();
            }
        }
    }

    public static class UlkomainenKansainvalinenYo {
        private static final String fieldPrefix = "pohjakoulutus_yo_ulkomainen";

        public final AsciiCountryCode maa;
        public final String tutkinto;

        private UlkomainenKansainvalinenYo(AsciiCountryCode maa, String tutkinto) {
            this.maa = maa;
            this.tutkinto = tutkinto;
        }

        public static Optional<UlkomainenKansainvalinenYo> of(Application application) {
            Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
            String valinta = baseEducation.get(fieldPrefix);

            if ("true".equals(valinta)) {
                String tutkinto = baseEducation.get(fieldPrefix + "_tutkinto");
                String maa = baseEducation.get(fieldPrefix + "_maa");
                variablesNotNull(tutkinto, maa);
                return Optional.of(new UlkomainenKansainvalinenYo(AsciiCountryCode.of(maa), tutkinto));
            } else {
                return Optional.absent();
            }
        }
    }

    public static class SuomalainenKansainvalinenYo {
        private static final String fieldPrefix = "pohjakoulutus_yo_kansainvalinen_suomessa";

        public final String tutkinto;

        private SuomalainenKansainvalinenYo(String tutkinto) {
            this.tutkinto = tutkinto;
        }

        public static Optional<SuomalainenKansainvalinenYo> of(Application application) {
            Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
            String valinta = baseEducation.get(fieldPrefix);

            if ("true".equals(valinta)) {
                String tutkinto = baseEducation.get(fieldPrefix + "_tutkinto");
                variablesNotNull(tutkinto);
                return Optional.of(new SuomalainenKansainvalinenYo(tutkinto));
            } else {
                return Optional.absent();
            }
        }
    }

    public static class SuomalainenYo {
        private static final String fieldPrefix = "pohjakoulutus_yo";

        public final String tutkinto;

        private SuomalainenYo(String tutkinto) {
            this.tutkinto = tutkinto;
        }

        public static Optional<SuomalainenYo> of(Application application) {
            Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
            String valinta = baseEducation.get(fieldPrefix);

            if ("true".equals(valinta)) {
                String tutkinto = baseEducation.get(fieldPrefix + "_tutkinto");
                variablesNotNull(tutkinto);
                return Optional.of(new SuomalainenYo(tutkinto));
            } else {
                return Optional.absent();
            }
        }
    }

    public static class SuomalainenYoAmmatillinen {
        private static final String fieldPrefix = "pohjakoulutus_yo_ammatillinen";

        public final String nimike;

        private SuomalainenYoAmmatillinen(String nimike) {
            this.nimike = nimike;
        }

        public static Optional<SuomalainenYoAmmatillinen> of(Application application) {
            Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
            String valinta = baseEducation.get(fieldPrefix);

            if ("true".equals(valinta)) {
                String nimike = baseEducation.get(fieldPrefix + "_nimike");
                variablesNotNull(nimike);
                return Optional.of(new SuomalainenYoAmmatillinen(nimike));
            } else {
                return Optional.absent();
            }
        }
    }

    public static class UlkomaalainenKoulutus {
        private static final String fieldPrefix = "pohjakoulutus_ulk";

        public final AsciiCountryCode maa;
        public final String nimike;

        private UlkomaalainenKoulutus(AsciiCountryCode maa, String nimike) {
            this.maa = maa;
            this.nimike = nimike;
        }

        public static Set<UlkomaalainenKoulutus> of(Application application) {
            Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
            String valinta = baseEducation.get(fieldPrefix);

            ImmutableSet.Builder<UlkomaalainenKoulutus> result = ImmutableSet.builder();

            if ("true".equals(valinta)) {
                for (int i = 1; ; i++) {
                    String index = (i == 1) ? "" : Integer.toString(i);
                    String nimike = baseEducation.get(fieldPrefix + "_nimike" + index);
                    String maa = baseEducation.get(fieldPrefix + "_suoritusmaa" + index);

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

    public static class UlkomaalainenKorkeakoulutus {
        private static final String fieldPrefix = "pohjakoulutus_kk_ulk";

        public final String taso;
        public final AsciiCountryCode maa;
        public final String nimike;

        private UlkomaalainenKorkeakoulutus(String taso, AsciiCountryCode maa, String nimike) {
            this.taso = taso;
            this.maa = maa;
            this.nimike = nimike;
        }

        public static Set<UlkomaalainenKorkeakoulutus> of(Application application) {
            Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
            String valinta = baseEducation.get(fieldPrefix);

            ImmutableSet.Builder<UlkomaalainenKorkeakoulutus> result = ImmutableSet.builder();

            if ("true".equals(valinta)) {
                for (int i = 1; ; i++) {
                    String index = (i == 1) ? "" : Integer.toString(i);
                    String taso = baseEducation.get(fieldPrefix + "_taso" + index);
                    String nimike = baseEducation.get(fieldPrefix + "_nimike" + index);
                    String maa = baseEducation.get(fieldPrefix + "_maa" + index);

                    if (variablesNotNull(taso, nimike, maa)) {
                        result.add(new UlkomaalainenKorkeakoulutus(taso, AsciiCountryCode.of(maa), nimike));
                    } else {
                        break;
                    }
                }
            }

            return result.build();
        }
    }

    public static class SuomalainenKorkeakoulutus {
        private static final String fieldPrefix = "pohjakoulutus_kk";

        public final String taso;
        public final String nimike;

        private SuomalainenKorkeakoulutus(String taso, String nimike) {
            this.taso = taso;
            this.nimike = nimike;
        }

        public static Set<SuomalainenKorkeakoulutus> of(Application application) {
            Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
            String valinta = baseEducation.get(fieldPrefix);

            ImmutableSet.Builder<SuomalainenKorkeakoulutus> result = ImmutableSet.builder();

            if ("true".equals(valinta)) {
                for (int i = 1; ; i++) {
                    String index = (i == 1) ? "" : Integer.toString(i);
                    String taso = baseEducation.get(fieldPrefix + "_taso" + index);
                    String nimike = baseEducation.get(fieldPrefix + "_nimike" + index);

                    if (variablesNotNull(taso, nimike)) {
                        result.add(new SuomalainenKorkeakoulutus(taso, nimike));
                    } else {
                        break;
                    }
                }
            }

            return result.build();
        }
    }
}
