package fi.vm.sade.haku.oppija.ui.common;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.AddressBuilder;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAttachment;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAttachmentBuilder;
import fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import fi.vm.sade.haku.virkailija.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.koulutusinformaatio.domain.dto.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class AttachmentUtil {

    public static List<ApplicationAttachment> resolveAttachments(Application application,
                                                                 KoulutusinformaatioService koulutusinformaatioService) {

        List<ApplicationAttachment> attachments = new ArrayList<ApplicationAttachment>();
        attachments = addApplicationOptionAttachments(attachments, application, koulutusinformaatioService);
        attachments = addDiscreationaryAttachments(attachments, application, koulutusinformaatioService);
        attachments = addHigherEdAttachments(attachments, application, koulutusinformaatioService);

        return attachments;
    }

    private static List<ApplicationAttachment> addApplicationOptionAttachments(
            List<ApplicationAttachment> attachments, Application application,
            KoulutusinformaatioService koulutusinformaatioService) {
        for (String aoOid : ApplicationUtil.getApplicationOptionAttachmentAOIds(application)) {
            ApplicationOptionDTO ao = koulutusinformaatioService.getApplicationOption(aoOid);
            for (ApplicationOptionAttachmentDTO attachmentDTO : ao.getAttachments()) {
                attachments.add(
                        ApplicationAttachmentBuilder.start()
                                .setName(ElementUtil.createI18NAsIs(attachmentDTO.getType()))
                                .setDescription(ElementUtil.createI18NAsIs(attachmentDTO.getDescreption()))
                                .setDeadline(attachmentDTO.getDueDate())
                                .setAddress(AddressBuilder.start()
                                        .setRecipient("")
                                        .setStreetAddress(attachmentDTO.getAddress().getStreetAddress())
                                        .setStreetAddress2(attachmentDTO.getAddress().getStreetAddress2())
                                        .setPostalCode(attachmentDTO.getAddress().getPostalCode())
                                        .setPostOffice(attachmentDTO.getAddress().getPostOffice())
                                        .build())
                                .build());
            }
        }
        return attachments;
    }

    private static List<ApplicationAttachment> addDiscreationaryAttachments(List<ApplicationAttachment> attachments,
                                                                            Application application,
                                                                            KoulutusinformaatioService koulutusinformaatioService) {
        for (String aoOid : ApplicationUtil.getDiscretionaryAttachmentAOIds(application)) {
            ApplicationOptionDTO ao = koulutusinformaatioService.getApplicationOption(aoOid);
            AddressDTO addressDTO = ao.getAttachmentDeliveryAddress();
            if (addressDTO == null) {
                addressDTO = ao.getProvider().getPostalAddress();
            }
            attachments.add(
                    ApplicationAttachmentBuilder.start()
                            .setName(ElementUtil.createI18NAsIs("Harkinnanvaraisuusliite"))
                            .setDescription(null)
                            .setDeadline(null)
                            .setAddress(AddressBuilder.start()
                                    .setRecipient(ao.getName())
                                    .setStreetAddress(addressDTO.getStreetAddress())
                                    .setStreetAddress2(addressDTO.getStreetAddress2())
                                    .setPostalCode(addressDTO.getPostalCode())
                                    .setPostOffice(addressDTO.getPostOffice())
                                    .build())
                            .build()
            );
        }
        return attachments;
    }


    private static List<ApplicationAttachment> addHigherEdAttachments(List<ApplicationAttachment> attachments,
                                                                      Application application,
                                                                      KoulutusinformaatioService koulutusinformaatioService) {
        Map<String, List<String>> higherEdAttachmentAOIds = getHigherEdAttachmentAOIds(application);
        Map<String, List<fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO>> higherEdAttachments =
                new HashMap<String, List<ApplicationOptionDTO>>();
        for (Map.Entry<String, List<String>> entry : higherEdAttachmentAOIds.entrySet()) {
            String key = entry.getKey();
            List<fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO> aos =
                    new ArrayList<ApplicationOptionDTO>();
            for (String aoOid : entry.getValue()) {
                ApplicationOptionDTO ao = koulutusinformaatioService.getApplicationOption(aoOid);
                ao = ensureAddress(ao);
                if (!addressAlreadyAdded(aos, ao)) {
                    aos.add(ao);
                }
            }
            higherEdAttachments.put(key, aos);
        }
        for (Map.Entry<String, List<ApplicationOptionDTO>> entry : higherEdAttachments.entrySet()) {
            String attachmentType = entry.getKey();
            for (ApplicationOptionDTO aoDTO : entry.getValue()) {
                AddressDTO addressDTO = null;
                String name = null;
                if (aoDTO.getProvider().getApplicationOffice() != null &&
                        aoDTO.getProvider().getApplicationOffice().getPostalAddress() != null) {
                    addressDTO = aoDTO.getProvider().getApplicationOffice().getPostalAddress();
                    name = aoDTO.getProvider().getApplicationOffice().getName();
                } else {
                    addressDTO = aoDTO.getProvider().getPostalAddress();
                    name = aoDTO.getProvider().getName();
                }

                attachments.add(
                        ApplicationAttachmentBuilder.start()
                                .setName(ElementUtil.createI18NAsIs(StringUtil.safeToString(aoDTO.getProvider().getName())))
                                .setDescription(ElementUtil.createI18NText("form.valmis.todistus." + attachmentType))
                                .setDeadline(null)
                                .setAddress(AddressBuilder.start()
                                        .setRecipient(name)
                                        .setStreetAddress(addressDTO.getStreetAddress())
                                        .setStreetAddress2(addressDTO.getStreetAddress2())
                                        .setPostalCode(addressDTO.getPostalCode())
                                        .setPostOffice(addressDTO.getPostOffice())
                                        .build())
                                .build()
                );
            }
        }
        return attachments;
    }

    private static ApplicationOptionDTO ensureAddress(ApplicationOptionDTO ao) {
        if (ao.getProvider().getApplicationOffice() != null
                && ao.getProvider().getApplicationOffice().getPostalAddress() != null) {
            return ao;
        }
        LearningOpportunityProviderDTO provider = ao.getProvider();
        ApplicationOfficeDTO office = provider.getApplicationOffice();
        if (office == null) {
            office = new ApplicationOfficeDTO();
            office.setName(provider.getName());
        }
        office.setPostalAddress(provider.getPostalAddress());
        provider.setApplicationOffice(office);
        return ao;
    }

    private static boolean addressAlreadyAdded(List<ApplicationOptionDTO> aos, ApplicationOptionDTO ao) {
        if (aos.isEmpty()) {
            return false;
        }
        ApplicationOfficeDTO newOffice = ao.getProvider().getApplicationOffice();
        for (ApplicationOptionDTO currAo : aos) {
            ApplicationOfficeDTO currOffice = currAo.getProvider().getApplicationOffice();
            if (StringUtils.equals(newOffice.getName(), currOffice.getName())
                    && StringUtils.equals(newOffice.getPostalAddress().getStreetAddress(),
                    currOffice.getPostalAddress().getStreetAddress())
                    && StringUtils.equals(newOffice.getPostalAddress().getStreetAddress2(),
                    currOffice.getPostalAddress().getStreetAddress2())
                    && StringUtils.equals(newOffice.getPostalAddress().getPostalCode(),
                    currOffice.getPostalAddress().getPostalCode())
                    && StringUtils.equals(newOffice.getPostalAddress().getPostOffice(),
                    currOffice.getPostalAddress().getPostOffice())) {
                return true;
            }
        }
        return false;
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
}
