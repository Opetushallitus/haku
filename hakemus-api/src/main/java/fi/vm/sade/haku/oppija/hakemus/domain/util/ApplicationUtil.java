package fi.vm.sade.haku.oppija.hakemus.domain.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.hakemus.domain.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.OPTION_ID_POSTFIX;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.PREFERENCE_PREFIX;
import static org.apache.commons.lang.StringUtils.isNotEmpty;


public final class ApplicationUtil {

    private ApplicationUtil() {
    }

    public static List<String> getPreferenceAoIds(final Application application){
        Map<String, String> answers = application.getVastauksetMerged();
        List<String> preferenceAoIds = new ArrayList<String>();
        for (String key: answers.keySet()){
            if (null != key && key.startsWith(PREFERENCE_PREFIX) && key.endsWith(OPTION_ID_POSTFIX) && isNotEmpty(answers.get(key))){
               preferenceAoIds.add(answers.get(key));
            }
        }
        return preferenceAoIds;
    }

    public static List<String> getDiscretionaryAttachmentAOIds(final Application application) {
        return getAttachmentAOIds(application, OppijaConstants.PREFERENCE_DISCRETIONARY);
    }

    public static List<String> getApplicationOptionAttachmentAOIds(Application application) {
        return getAttachmentAOIds(application, "preference%d-Koulutus-id-attachments");
    }

    public static Map<String, List<String>> getHigherEdAttachmentAOIds(Application application) {

        Map<String, List<String>> attachments = new LinkedHashMap<String, List<String>>();

        List<String> universityAOs = getUniversityAOs(application);
        List<String> amkAOs = getAmkAOs(application);
        List<String> aspaAmkAOs = getAspaAmkAOs(application);

        List<String> universityAndAspaAmkAOs = new ArrayList<String>();
        universityAndAspaAmkAOs.addAll(universityAOs);
        universityAndAspaAmkAOs.addAll(aspaAmkAOs);

        List<String> allAOs = new ArrayList<String>();
        allAOs.addAll(universityAOs);
        allAOs.addAll(amkAOs);
        if (yoNeeded(application) && !universityAOs.isEmpty()) {
            attachments.put("yo", universityAOs);
        }
        if (hasBaseEducation(application, "pohjakoulutus_am") && !universityAOs.isEmpty()) {
            attachments.put("am", universityAOs);
        }
        if (hasBaseEducation(application, "pohjakoulutus_amt") && !universityAndAspaAmkAOs.isEmpty()) {
            attachments.put("amt", universityAndAspaAmkAOs);
        }
        if (hasBaseEducation(application, "pohjakoulutus_kk") && !allAOs.isEmpty()) {
            attachments.put("kk", allAOs);
        }
        if (hasBaseEducation(application, "pohjakoulutus_ulk") && !universityAndAspaAmkAOs.isEmpty()) {
            attachments.put("ulk", universityAndAspaAmkAOs);
        }
        if (hasBaseEducation(application, "pohjakoulutus_avoin") && !allAOs.isEmpty()) {
            attachments.put("avoin", allAOs);
        }
        if (hasBaseEducation(application, "pohjakoulutus_muu") && !allAOs.isEmpty()) {
            attachments.put("muu", allAOs);
        }
        return attachments;
    }

    public static Map<String, List<String>> getAmkOpeAttachments(Application application) {
        Map<String, List<String>> attachments = new LinkedHashMap<String, List<String>>();
        Map<String, String> koulutustaustaAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
        String tutkintotaso = koulutustaustaAnswers.get("amk_ope_tutkinnontaso");
        List<String> eiKorkeakoulututkinto = new ArrayList<String>() {{
            add("opisto"); add("ammatillinen"); add("ammatti"); add("muu");
        }};
        if (eiKorkeakoulututkinto.contains(tutkintotaso)) {
            attachments.put("ei_korkeakoulututkintoa", getPreferenceAoIds(application));
            if ("opettajana_ammatillisessa_tutkinto".equals(koulutustaustaAnswers.get("ei_korkeakoulututkintoa"))) {
                attachments.put("opettajana_ammatillisessa_tutkinto", getPreferenceAoIds(application));
            } else if ("opettajana_ammatillisessa".equals(koulutustaustaAnswers.get("ei_korkeakoulututkintoa"))) {
                attachments.put("opettajana_ammatillisessa", getPreferenceAoIds(application));
            }
        } else if ("ulk".equals(tutkintotaso)) {
            attachments.put("ulkomainen", getPreferenceAoIds(application));
        }
        return attachments;
    }



    private static boolean yoNeeded(Application application) {
        if (!hasBaseEducation(application, "pohjakoulutus_yo")) {
            return false;
        }
        Map<String, String> answers = application.getVastauksetMerged();
        int suoritusvuosi = Integer.parseInt(answers.get("pohjakoulutus_yo_vuosi"));
        if (suoritusvuosi < 1990) {
            return true;
        }
        String tutkinto = answers.get("pohjakoulutus_yo_tutkinto");
        if ("fi".equals(tutkinto)) {
            return false;
        }
        return true;

    }


    private static List<String> getAmkAOs(Application application) {
        return getAosForType(application, "amkLiite");
    }

    private static List<String> getUniversityAOs(Application application) {
        return getAosForType(application, "yoLiite");
    }

    private static List<String> getAosForType(Application application, String liiteKeyBase) {
        Map<String, String> answers = application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        List<String> providers = new ArrayList<String>();
        List<String> aos = new ArrayList<String>();
        int i = 1;
        while (true) {
            String aoKey = String.format(OppijaConstants.PREFERENCE_ID, i);
            if (!answers.containsKey(aoKey)) {
                break;
            }
            String aoId = answers.get(aoKey);
            String provider = answers.get("preference" + i + "-Opetuspiste-id");
            String liiteKey = "preference" + i + "-" + liiteKeyBase;
            if (isIdGivenAndKeyValueTrue(answers, aoId, liiteKey)
                    && !aos.contains(aoId)
                    && !providers.contains(provider)) {
                aos.add(aoId);
                providers.add(provider);
            }
            i++;
        }
        return aos;
    }

    private static boolean hasBaseEducation(Application application, String field) {
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

    private static List<String> getAspaAmkAOs(Application application) {
        Set<String> aspaAos = new HashSet<String>();
        Map<String, String> answers = application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        List<String> aos = new ArrayList<String>();
        int i = 1;
        while (true) {
            String aoKey = String.format(OppijaConstants.PREFERENCE_ID, i);
            if (!answers.containsKey(aoKey)) {
                break;
            }
            String aoId = answers.get(aoKey);
            String liiteKey = "preference" + i + "-amkLiite";
            if (isIdGivenAndKeyValueTrue(answers, aoId, liiteKey) && !aos.contains(aoId)) {
                String groupsStr = answers.get("preference" + i + "-Koulutus-id-attachmentgroups");
                if (StringUtils.isBlank(groupsStr)) {
                    aos.add(aoId);
                } else {
                    for (String group : groupsStr.split(",")) {
                        if (!aspaAos.contains(group)) {
                            aspaAos.add(group);
                            aos.add(aoId);
                        }
                    }
                }
            }
            i++;
        }
        return aos;
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
}
