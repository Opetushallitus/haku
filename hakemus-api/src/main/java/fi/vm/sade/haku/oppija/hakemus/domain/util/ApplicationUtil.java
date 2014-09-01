package fi.vm.sade.haku.oppija.hakemus.domain.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


public final class ApplicationUtil {

    private ApplicationUtil() {
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
            String key = String.format(OppijaConstants.PREFERENCE_ID, i);
            if (!answers.containsKey(key)) {
                break;
            }
            String liiteKey = "preference" + i + "-" + liiteKeyBase;
            String provider = answers.get("preference" + i + "-Opetuspiste-id");
            if (answers.containsKey(liiteKey)
                    && !aos.contains(answers.get(key))
                    && !providers.contains(provider)) {
                aos.add(answers.get(key));
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
            String key = String.format(OppijaConstants.PREFERENCE_ID, i);
            if (answers.containsKey(key)) {
                String aoId = answers.get(key);
                String discretionaryKey = String.format(field, i);
                if (!Strings.isNullOrEmpty(aoId) && answers.containsKey(discretionaryKey)) {
                    String discretionaryValue = answers.get(discretionaryKey);
                    if (!Strings.isNullOrEmpty(discretionaryValue) && Boolean.parseBoolean(discretionaryValue)) {
                        attachmentAOs.add(aoId);
                    }
                }
            } else {
                break;
            }
            ++i;
        }
        return attachmentAOs;
    }


    private static List<String> getAspaAmkAOs(Application application) {
        Set<String> aspaAos = new HashSet<String>();
        Map<String, String> answers = application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        List<String> aos = new ArrayList<String>();
        int i = 1;
        while (true) {
            String key = String.format(OppijaConstants.PREFERENCE_ID, i);
            if (!answers.containsKey(key)) {
                break;
            }
            String liiteKey = "preference" + i + "-amkLiite";
            if (answers.containsKey(liiteKey) && !aos.contains(key)) {
                String groupsStr = answers.get("preference" + i + "-Koulutus-id-attachmentgroups");
                if (StringUtils.isBlank(groupsStr)) {
                    aos.add(answers.get(key));
                } else {
                    for (String group : groupsStr.split(",")) {
                        if (!aspaAos.contains(group)) {
                            aspaAos.add(group);
                            aos.add(answers.get(key));
                        }
                    }
                }
            }
            i++;
        }
        return aos;
    }

}
