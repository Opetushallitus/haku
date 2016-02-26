package fi.vm.sade.haku.oppija.hakemus.domain.util;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.haku.oppija.hakemus.domain.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.MergedAnswers;

import javax.annotation.Nullable;
import java.util.*;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.OPTION_ID_POSTFIX;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.PREFERENCE_PREFIX;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public final class ApplicationUtil {

    private ApplicationUtil() {
    }

    public static List<String> getPreferenceAoIds(final Application application){
        return getPreferenceAoIds(MergedAnswers.of(application.getVastauksetMerged()));
    }

    private static int preferenceOrdinal(String preferenceId) {
        return Integer.valueOf(preferenceId.replace(PREFERENCE_PREFIX, "").replace(OPTION_ID_POSTFIX, ""));
    }

    public static List<String> getPreferenceAoIds(final MergedAnswers answers) {
        SortedSet<String> preferenceKeys = new TreeSet<>(new Comparator<String>() {
            public int compare(String k, String k1) {
                return Integer.compare(preferenceOrdinal(k), preferenceOrdinal(k1));
            }
        });
        for (String key : answers.getValue().keySet()) {
            if (null != key && key.startsWith(PREFERENCE_PREFIX) && key.endsWith(OPTION_ID_POSTFIX) && isNotEmpty(answers.get(key))) {
               preferenceKeys.add(key);
            }
        }
        final List<String> preferenceAoIds = new ArrayList<>();
        for (String key : preferenceKeys) {
            preferenceAoIds.add(answers.get(key));
        }
        return preferenceAoIds;
    }

    public static List<String> getDiscretionaryAttachmentAOIds(final Application application) {
        return getAttachmentAOIds(application, OppijaConstants.PREFERENCE_DISCRETIONARY);
    }

    public static Map<String, List<String>> getAmkOpeAttachments(final Application application) {
        Map<String, List<String>> attachments = new LinkedHashMap<String, List<String>>();
        List<String> aoIds = new ArrayList<String> () {{ add(getFirstAoId(application)); }};

        Map<String, String> koulutustaustaAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);

        String tutkintotaso = koulutustaustaAnswers.get("amk_ope_tutkinnontaso");
        if (isNotBlank(tutkintotaso)) {
            // Liite 1. Tutkinto, jolla haet: kopio tutkintotodistuksestasi ja tarvittaessa kopio rinnastamispäätöksestä
            attachments.put("form.valmis.amkope.tutkintotodistus", aoIds);
        }

        List<String> eiKorkeakoulututkinto = new ArrayList<String>() {{
            add("opisto"); add("ammatillinen"); add("ammatti"); add("muu");
        }};

        if (eiKorkeakoulututkinto.contains(tutkintotaso)) {
            if ("opettajana_ammatillisessa_tutkinto".equals(koulutustaustaAnswers.get("ei_korkeakoulututkintoa"))
                    || "opettajana_ammatillisessa".equals(koulutustaustaAnswers.get("ei_korkeakoulututkintoa"))) {
                // Liite 2. Oppilaitoksen/työnantajan lausunto, https://opintopolku.fi/wp/wp-content/uploads/2014/12/2015_Oppilaitoksen_lausunto.pdf (laita linkki aukeamaan uuteen ikkunaan)
                attachments.put("form.valmis.amkope.tyonantajan_lausunto", aoIds);
            }
        }

        String pedagogisetOpinnot = koulutustaustaAnswers.get("pedagogiset_opinnot");
        if (isNotBlank(pedagogisetOpinnot) && pedagogisetOpinnot.equals("true")) {
            // Liite 3. Opettajan pedagogiset opinnot: kopio todistuksestasi
            attachments.put("form.valmis.amkope.pedagogiset_opinnot", aoIds);
        }

        for (String t : new String[] {"amt", "am", "kk", "tri"} ) {
            String muuTutkintoId = "muu_tutkinto_" + t;
            String muuTutkinto = koulutustaustaAnswers.get(muuTutkintoId);
            if ("true".equals(muuTutkinto)) {
                attachments.put("form.valmis.amkope.muu_tutkinto", aoIds);
                break;
            }
        }

        return attachments;
    }

    private static String getFirstAoId(Application application) {
        Map<String, String> preferenceAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        String aoKey = String.format(OppijaConstants.PREFERENCE_ID, 1);
        return preferenceAnswers.get(aoKey);
    }

    public static boolean hasBaseEducationYo(Application application) {
        if (!hasBaseEducation(application, "pohjakoulutus_yo")) {
            return false;
        }
        String tutkinto = application.getVastauksetMerged().get("pohjakoulutus_yo_tutkinto");
        return "fi".equals(tutkinto) || "lkOnly".equals(tutkinto);
    }

    public static boolean hasBaseEducationYoOrKvYo(Application application) {
        return hasBaseEducationYo(application)
                || hasBaseEducation(application, "pohjakoulutus_yo_kansainvalinen_suomessa")
                || hasBaseEducation(application, "pohjakoulutus_yo_ulkomainen");
    }

    public static boolean hasBaseEducationLukio(Application application) {
        return hasBaseEducation(application, "pohjakoulutus_yo") && !hasBaseEducationYo(application);
    }

    public static int yoSuoritusvuosi(Application application) {
        return Integer.parseInt(application.getVastauksetMerged().get("pohjakoulutus_yo_vuosi"));
    }

    public static boolean hasBaseEducation(Application application, String field) {
        Map<String, String> answers = application.getVastauksetMerged();
        return Boolean.parseBoolean(answers.get(field));
    }

    private static List<String> getAttachmentAOIds(Application application, String field) {
        List<String> attachmentAOs = Lists.newArrayList();
        Map<String, String> answers = application.getVastauksetMerged();
        int i = 1;
        while (true) {
            String aoKey = String.format(OppijaConstants.PREFERENCE_ID, i);
            if (answers.containsKey(aoKey)) {
                String aoId = answers.get(aoKey);
                String key = String.format(field, i);
                if (isIdGivenAndKeyValueTrue(answers, aoId, key)) {
                    attachmentAOs.add(aoId);
                }
            } else {
                break;
            }
            ++i;
        }
        return attachmentAOs;
    }

    private static boolean isIdGivenAndKeyValueTrue(Map<String, String> answers, String id, String key) {
        if (!Strings.isNullOrEmpty(id) && answers.containsKey(key)) {
            String value = answers.get(key);
            if (!Strings.isNullOrEmpty(value) && Boolean.parseBoolean(value)) {
                return true;
            }
        }
        return false;
    }

    public static List<PreferenceEligibility> checkAndCreatePreferenceEligibilities(List<PreferenceEligibility> existingPreferenceEligibilities, List<String> preferenceAoIds) {
        List<PreferenceEligibility> currentPreferenceEligibilities = new ArrayList<PreferenceEligibility>(preferenceAoIds.size());
        PreferenceEligibility matchingPreferenceEligibility = null;
        for (String preferenceAoId : preferenceAoIds) {
            for (PreferenceEligibility preferenceEligibility : existingPreferenceEligibilities){
                if (preferenceAoId.equals(preferenceEligibility.getAoId())) {
                    matchingPreferenceEligibility = preferenceEligibility;
                    break;
                }
            }
            if (null == matchingPreferenceEligibility)
                matchingPreferenceEligibility = PreferenceEligibilityBuilder.start().setAoId(preferenceAoId).build();
            currentPreferenceEligibilities.add(matchingPreferenceEligibility);
            matchingPreferenceEligibility = null;
        }
        return currentPreferenceEligibilities;
    }

    public static List<PreferenceChecked> checkAndCreatePreferenceCheckedData(List<PreferenceChecked> existingPreferencesChecked, List<String> preferenceAoIds) {
        List<PreferenceChecked> currentPreferencesChecked = new ArrayList<PreferenceChecked>(preferenceAoIds.size());
        PreferenceChecked matchingPreferenceChecked = null;
        for (String preferenceAoId : preferenceAoIds) {
            for (PreferenceChecked preferenceChecked : existingPreferencesChecked){
                if (preferenceAoId.equals(preferenceChecked.getPreferenceAoOid())) {
                    matchingPreferenceChecked = preferenceChecked;
                    break;
                }
            }
            if (null == matchingPreferenceChecked)
                matchingPreferenceChecked = PreferenceCheckedBuilder.start().setPreferenceAoOid(preferenceAoId).build();
            currentPreferencesChecked.add(matchingPreferenceChecked);
            matchingPreferenceChecked = null;
        }
        return currentPreferencesChecked;
    }

    public static String getApplicationOptionName(Application application, PreferenceEligibility preferenceEligibility) {
        Map<String, String> aoAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        for (int i = 1; i < 100; i++) {
            String aoPrefix = String.format("preference%d-", i);
            String aoKey = String.format("%sKoulutus-id", aoPrefix);
            String aoOid = aoAnswers.get(aoKey);
            if (isBlank(aoOid)) {
                break;
            }
            if(preferenceEligibility.getAoId().equalsIgnoreCase(aoOid)) {
                String koulutusKey = String.format("%sKoulutus", aoPrefix);
                String opetuspisteKey = String.format("%sOpetuspiste", aoPrefix);

                String koulutus = aoAnswers.get(koulutusKey);
                String opetuspiste = aoAnswers.get(opetuspisteKey);

                String applicationOptionName = "";
                if(!isBlank(koulutus)) {
                    applicationOptionName += koulutus;
                }
                if(!isBlank(applicationOptionName)) {
                    applicationOptionName += ", ";
                }
                if(!isBlank(opetuspiste)) {
                    applicationOptionName += opetuspiste;
                }

                return applicationOptionName;
            }
        }
        return preferenceEligibility.getAoId();
    }
}
