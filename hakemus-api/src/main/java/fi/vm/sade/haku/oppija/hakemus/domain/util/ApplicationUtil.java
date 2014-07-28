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
        //AOs requiring attachments
        List<String> discretionaryAttachmentAOs = Lists.newArrayList();
        Map<String, String> answers = application.getVastauksetMerged();
        int i = 1;
        while (true) {
            String key = String.format(OppijaConstants.PREFERENCE_ID, i);
            if (answers.containsKey(key)) {
                String aoId = answers.get(key);
                String discretionaryKey = String.format(OppijaConstants.PREFERENCE_DISCRETIONARY, i);
                if (!Strings.isNullOrEmpty(aoId) && answers.containsKey(discretionaryKey)) {
                    String discretionaryValue = answers.get(discretionaryKey);
                    if (!Strings.isNullOrEmpty(discretionaryValue) && Boolean.parseBoolean(discretionaryValue)) {
                        discretionaryAttachmentAOs.add(aoId);
                    }
                }
            } else {
                break;
            }
            ++i;
        }
        return discretionaryAttachmentAOs;
    }

    public static Map<String, List<String>> getHigherEdAttachmentAOIds(Application application) {

        Map<String, List<String>> attachments = new HashMap<String, List<String>>();

        List<String> universityAOs = getUniversityAOs(application);
        List<String> amkAOs = getAmkAOs(application);
        List<String> aspaAmkAOs = getAspaAmkAOs(application);
        List<String> universityAndAspaAmkAOs = new ArrayList<String>();
        universityAndAspaAmkAOs.addAll(universityAOs);
        universityAndAspaAmkAOs.addAll(aspaAmkAOs);
        List<String> allAOs = new ArrayList<String>();
        allAOs.addAll(universityAOs);
        allAOs.addAll(amkAOs);

        if (yoNeeded(application)) {
            attachments.put("yo", universityAOs);
        }
        if (hasBaseEducation(application, "pohjakoulutus_am")) {
            attachments.put("am", universityAOs);
        }
        if (hasBaseEducation(application, "pohjakoulutus_amt")) {
            attachments.put("amt", universityAndAspaAmkAOs);
        }
        if (hasBaseEducation(application, "pohjakoulutus_kk")) {
            attachments.put("kk", allAOs);
        }
        if (hasBaseEducation(application, "pohjakoulutus_ulk")) {
            attachments.put("ulk", universityAndAspaAmkAOs);
        }
        if (hasBaseEducation(application, "pohjakoulutus_avoin")) {
            attachments.put("avoin", allAOs);
        }
        if (hasBaseEducation(application, "pohjakoulutus_muu")) {
            attachments.put("muu", allAOs);
        }
        return attachments;
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
                String groupsStr = answers.get("preference" + 1 + "-Koulutus-id-attachmentgroups");
                if (StringUtils.isBlank(groupsStr)) {
                    aos.add(key);
                } else {
                    for (String group : groupsStr.split(",")) {
                        if (!aspaAos.contains(group)) {
                            aspaAos.add(group);
                            aos.add(key);
                        }
                    }
                }
            }
            i++;
        }
        return aos;
    }

    private static List<String> getAmkAOs(Application application) {
        return getAosForType(application, "amkLiite");
    }

    private static List<String> getUniversityAOs(Application application) {
        return getAosForType(application, "yoLiite");
    }

    private static List<String> getAosForType(Application application, String liiteKeyBase) {
        Map<String, String> answers = application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);
        List<String> aos = new ArrayList<String>();
        int i = 1;
        while (true) {
            String key = String.format(OppijaConstants.PREFERENCE_ID, i);
            if (!answers.containsKey(key)) {
                break;
            }
            String liiteKey = "preference" + i + "-" + liiteKeyBase;
            if (answers.containsKey(liiteKey) && !aos.contains(key)) {
                aos.add(key);
            }
            i++;
        }
        return aos;
    }

    private static boolean hasBaseEducation(Application application, String field) {
        Map<String, String> answers = application.getVastauksetMerged();
        return Boolean.parseBoolean(answers.get(field));
    }

    private static boolean yoNeeded(Application application) {
        if (!hasBaseEducation(application, "pohjakoulutus_yo")) {
            return false;
        }
        Map<String, String> answers = application.getVastauksetMerged();
        boolean hasYo = Boolean.parseBoolean(answers.get("pohjakoulutus_yo"));
        if (!hasYo) {
            return false;
        }
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
}