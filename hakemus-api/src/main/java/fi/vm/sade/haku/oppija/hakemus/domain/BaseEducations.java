package fi.vm.sade.haku.oppija.hakemus.domain;

import com.google.common.base.Function;
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

    public interface ProvideNimike {
        public String getNimike();
    }

    public static class SuomalainenAvoinKoulutus implements ProvideNimike {
        private static final String fieldPrefix = "pohjakoulutus_avoin";

        public final String nimike;

        @Override
        public String getNimike() {
            return nimike;
        }

        private SuomalainenAvoinKoulutus(String nimike) {
            this.nimike = nimike;
        }

        public static final Function<Application, Set<SuomalainenAvoinKoulutus>> of = new Function<Application, Set<SuomalainenAvoinKoulutus>>() {
            @Override
            public Set<SuomalainenAvoinKoulutus> apply(Application application) {
                Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
                String valinta = baseEducation.get(fieldPrefix);

                if ("true".equals(valinta)) {
                    String nimike = baseEducation.get(fieldPrefix + "_kokonaisuus");
                    variablesNotNull(nimike);
                    return ImmutableSet.of(new SuomalainenAvoinKoulutus(nimike));
                } else {
                    return ImmutableSet.of();
                }
            }
        };
    }

    // TODO: in reality a list
    public static class SuomalainenAmKoulutus implements ProvideNimike {
        private static final String fieldPrefix = "pohjakoulutus_am";

        public final String nimike;

        @Override
        public String getNimike() {
            return nimike;
        }

        private SuomalainenAmKoulutus(String nimike) {
            this.nimike = nimike;
        }

        public static final Function<Application, Set<SuomalainenAmKoulutus>> of = new Function<Application, Set<SuomalainenAmKoulutus>>() {
            @Override
            public Set<SuomalainenAmKoulutus> apply(Application application) {
                Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
                String valinta = baseEducation.get(fieldPrefix);

                if ("true".equals(valinta)) {
                    String nimike = baseEducation.get(fieldPrefix + "_nimike");
                    variablesNotNull(nimike);
                    return ImmutableSet.of(new SuomalainenAmKoulutus(nimike));
                } else {
                    return ImmutableSet.of();
                }
            }
        };
    }

    // TODO: in reality a list
    public static class SuomalainenAmtKoulutus implements ProvideNimike {
        private static final String fieldPrefix = "pohjakoulutus_amt";

        public final String nimike;

        @Override
        public String getNimike() {
            return nimike;
        }

        private SuomalainenAmtKoulutus(String nimike) {
            this.nimike = nimike;
        }

        public static final Function<Application, Set<SuomalainenAmtKoulutus>> of = new Function<Application, Set<SuomalainenAmtKoulutus>>() {
            @Override
            public Set<SuomalainenAmtKoulutus> apply(Application application) {
                Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
                String valinta = baseEducation.get(fieldPrefix);

                if ("true".equals(valinta)) {
                    String nimike = baseEducation.get(fieldPrefix + "_nimike");
                    variablesNotNull(nimike);
                    return ImmutableSet.of(new SuomalainenAmtKoulutus(nimike));
                } else {
                    return ImmutableSet.of();
                }
            }
        };
    }

    public static class UlkomainenKansainvalinenYo {
        private static final String fieldPrefix = "pohjakoulutus_yo_ulkomainen";

        public final AsciiCountryCode maa;
        public final String tutkinto;

        private UlkomainenKansainvalinenYo(AsciiCountryCode maa, String tutkinto) {
            this.maa = maa;
            this.tutkinto = tutkinto;
        }

        public static final Function<Application, Set<UlkomainenKansainvalinenYo>> of = new Function<Application, Set<UlkomainenKansainvalinenYo>>() {
            @Override
            public Set<UlkomainenKansainvalinenYo> apply(Application application) {
                Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
                String valinta = baseEducation.get(fieldPrefix);

                if ("true".equals(valinta)) {
                    String tutkinto = baseEducation.get(fieldPrefix + "_tutkinto");
                    String maa = baseEducation.get(fieldPrefix + "_maa");
                    variablesNotNull(tutkinto, maa);
                    return ImmutableSet.of(new UlkomainenKansainvalinenYo(AsciiCountryCode.of(maa), tutkinto));
                } else {
                    return ImmutableSet.of();
                }
            }
        };
    }

    public static class SuomalainenKansainvalinenYo {
        private static final String fieldPrefix = "pohjakoulutus_yo_kansainvalinen_suomessa";

        public final String tutkinto;

        private SuomalainenKansainvalinenYo(String tutkinto) {
            this.tutkinto = tutkinto;
        }

        public static final Function<Application, Set<SuomalainenKansainvalinenYo>> of = new Function<Application, Set<SuomalainenKansainvalinenYo>>() {
            @Override
            public Set<SuomalainenKansainvalinenYo> apply(Application application) {
                Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
                String valinta = baseEducation.get(fieldPrefix);

                if ("true".equals(valinta)) {
                    String tutkinto = baseEducation.get(fieldPrefix + "_tutkinto");
                    variablesNotNull(tutkinto);
                    return ImmutableSet.of(new SuomalainenKansainvalinenYo(tutkinto));
                } else {
                    return ImmutableSet.of();
                }
            }
        };
    }

    public static class SuomalainenYo {
        private static final String fieldPrefix = "pohjakoulutus_yo";

        public final String tutkinto;

        private SuomalainenYo(String tutkinto) {
            this.tutkinto = tutkinto;
        }

        public static final Function<Application, Set<SuomalainenYo>> of = new Function<Application, Set<SuomalainenYo>>() {
            @Override
            public Set<SuomalainenYo> apply(Application application) {
                Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
                String valinta = baseEducation.get(fieldPrefix);

                if ("true".equals(valinta)) {
                    String tutkinto = baseEducation.get(fieldPrefix + "_tutkinto");
                    variablesNotNull(tutkinto);
                    return ImmutableSet.of(new SuomalainenYo(tutkinto));
                } else {
                    return ImmutableSet.of();
                }
            }
        };
    }

    public static class SuomalainenYoAmmatillinen implements ProvideNimike {
        private static final String fieldPrefix = "pohjakoulutus_yo_ammatillinen";

        public final String nimike;

        @Override
        public String getNimike() {
            return nimike;
        }

        private SuomalainenYoAmmatillinen(String nimike) {
            this.nimike = nimike;
        }

        public static final Function<Application, Set<SuomalainenYoAmmatillinen>> of = new Function<Application, Set<SuomalainenYoAmmatillinen>>() {
            @Override
            public Set<SuomalainenYoAmmatillinen> apply(Application application) {
                Map<String, String> baseEducation = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
                String valinta = baseEducation.get(fieldPrefix);

                if ("true".equals(valinta)) {
                    String nimike = baseEducation.get(fieldPrefix + "_nimike");
                    variablesNotNull(nimike);
                    return ImmutableSet.of(new SuomalainenYoAmmatillinen(nimike));
                } else {
                    return ImmutableSet.of();
                }
            }
        };
    }

    public static class UlkomaalainenKoulutus implements ProvideNimike {
        private static final String fieldPrefix = "pohjakoulutus_ulk";

        public final AsciiCountryCode maa;
        public final String nimike;

        @Override
        public String getNimike() {
            return nimike;
        }

        private UlkomaalainenKoulutus(AsciiCountryCode maa, String nimike) {
            this.maa = maa;
            this.nimike = nimike;
        }

        public static final Function<Application, Set<UlkomaalainenKoulutus>> of = new Function<Application, Set<UlkomaalainenKoulutus>>() {
            @Override
            public Set<UlkomaalainenKoulutus> apply(Application application) {

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
        };
    }

    public static class UlkomaalainenKorkeakoulutus implements ProvideNimike {
        private static final String fieldPrefix = "pohjakoulutus_kk_ulk";

        public final String taso;
        public final AsciiCountryCode maa;
        public final String nimike;

        @Override
        public String getNimike() {
            return nimike;
        }

        private UlkomaalainenKorkeakoulutus(String taso, AsciiCountryCode maa, String nimike) {
            this.taso = taso;
            this.maa = maa;
            this.nimike = nimike;
        }

        public static final Function<Application, Set<UlkomaalainenKorkeakoulutus>> of = new Function<Application, Set<UlkomaalainenKorkeakoulutus>>() {
            @Override
            public Set<UlkomaalainenKorkeakoulutus> apply(Application application) {

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
        };
    }

    public static class SuomalainenKorkeakoulutus implements ProvideNimike {
        private static final String fieldPrefix = "pohjakoulutus_kk";

        public final String taso;
        public final String nimike;

        @Override
        public String getNimike() {
            return nimike;
        }

        private SuomalainenKorkeakoulutus(String taso, String nimike) {
            this.taso = taso;
            this.nimike = nimike;
        }

        public static final Function<Application, Set<SuomalainenKorkeakoulutus>> of = new Function<Application, Set<SuomalainenKorkeakoulutus>>() {
            @Override
            public Set<SuomalainenKorkeakoulutus> apply(Application application) {
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
        };
    }
}
