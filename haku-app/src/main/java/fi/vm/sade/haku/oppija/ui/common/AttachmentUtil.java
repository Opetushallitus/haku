package fi.vm.sade.haku.oppija.ui.common;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.AddressBuilder;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAttachment;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAttachmentBuilder;
import fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationOptionAttachmentRequest;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import fi.vm.sade.haku.virkailija.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.SimpleAddress;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.koulutusinformaatio.domain.dto.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class AttachmentUtil {

    public static List<ApplicationAttachment> resolveAttachments(ApplicationSystem applicationSystem, Application application,
                                                                 KoulutusinformaatioService koulutusinformaatioService) {

        String lang = application.getMetaValue(Application.META_FILING_LANGUAGE);
        if (lang == null) {
            Map<String, String> miscAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_MISC);
            lang = "fi";
            if (miscAnswers != null) {
                String contactLang = miscAnswers.get(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE);
                if ("ruotsi".equals(contactLang)) {
                    lang = "sv";
                } else if ("englanti".equals(contactLang)) {
                    lang = "en";
                }
            }
        }
        
        List<ApplicationAttachment> attachments = new ArrayList<ApplicationAttachment>();
        attachments = addApplicationOptionAttachments(attachments, application, koulutusinformaatioService, lang);
        attachments = addDiscreationaryAttachments(attachments, application, koulutusinformaatioService, lang);
        attachments = addHigherEdAttachments(attachments, application, koulutusinformaatioService, lang);
        attachments = addApplicationOptionAttachmentRequests(attachments, application, applicationSystem);

        return attachments;
    }

    private static List<ApplicationAttachment> addApplicationOptionAttachmentRequests(List<ApplicationAttachment> attachments,
                                                                                      Application application,
                                                                                      ApplicationSystem applicationSystem) {
        for (ApplicationOptionAttachmentRequest attachmentRequest : applicationSystem.getApplicationOptionAttachmentRequests()){
            if (attachmentRequest.include(application.getVastauksetMerged())){
                SimpleAddress address = attachmentRequest.getDeliveryAddress();
                attachments.add(ApplicationAttachmentBuilder.start()
                  .setHeader(attachmentRequest.getHeader())
                  .setDescription(attachmentRequest.getDescription())
                  .setDeadline(attachmentRequest.getDeliveryDue())
                  .setAddress(AddressBuilder.start()
                    .setRecipient(address.getRecipient())
                    .setStreetAddress(address.getStreet())
                    .setPostalCode(address.getPostCode())
                    .setPostOffice(address.getPostOffice())
                    .build())
                  .build());
            }
        }
        return attachments;
    }

    private static List<ApplicationAttachment> addApplicationOptionAttachments(
            List<ApplicationAttachment> attachments, Application application,
            KoulutusinformaatioService koulutusinformaatioService, String lang) {
        for (String aoOid : ApplicationUtil.getApplicationOptionAttachmentAOIds(application)) {
            ApplicationOptionDTO ao = koulutusinformaatioService.getApplicationOption(aoOid, lang);
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
                                                                            KoulutusinformaatioService koulutusinformaatioService,
                                                                            String lang) {

        for (String aoOid : ApplicationUtil.getDiscretionaryAttachmentAOIds(application)) {
            ApplicationOptionDTO ao = koulutusinformaatioService.getApplicationOption(aoOid, lang);
            AddressDTO addressDTO = ao.getAttachmentDeliveryAddress();
            if (addressDTO == null) {
                addressDTO = ao.getProvider().getPostalAddress();
            }
            Map<String, String> answers = application.getPhaseAnswers(OppijaConstants.PHASE_APPLICATION_OPTIONS);
            String discreationaryReason = null;
            for (Map.Entry<String, String> entry : answers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.endsWith("-Koulutus-id") && value.equals(aoOid)) {
                    String prefix = key.substring(0, key.indexOf("-"));
                    discreationaryReason = answers.get(prefix + "-discretionary-follow-up");
                    break;
                }
            }

            ApplicationAttachmentBuilder attachmentBuilder = ApplicationAttachmentBuilder.start()
                    .setName(ElementUtil.createI18NText("form.valmis.liitteet.harkinnanvaraisuus"))
                    .setDeadline(null)
                    .setAddress(AddressBuilder.start()
                            .setRecipient(ao.getProvider().getName() + " " + ao.getName())
                            .setStreetAddress(addressDTO.getStreetAddress())
                            .setStreetAddress2(addressDTO.getStreetAddress2())
                            .setPostalCode(addressDTO.getPostalCode())
                            .setPostOffice(addressDTO.getPostOffice())
                            .build());
            if (discreationaryReason != null) {
                attachmentBuilder.setDescription(ElementUtil.createI18NText("form.valmis.liitteet.harkinnanvaraisuus."
                        +discreationaryReason));
            }

            attachments.add(attachmentBuilder.build());
        }
        return attachments;
    }


    private static List<ApplicationAttachment> addHigherEdAttachments(List<ApplicationAttachment> attachments,
                                                                      Application application,
                                                                      KoulutusinformaatioService koulutusinformaatioService,
                                                                      String lang) {
        Map<String, List<String>> higherEdAttachmentAOIds = ApplicationUtil.getHigherEdAttachmentAOIds(application);
        Map<String, List<ApplicationOptionDTO>> higherEdAttachments =
                new HashMap<String, List<ApplicationOptionDTO>>();
        for (Map.Entry<String, List<String>> entry : higherEdAttachmentAOIds.entrySet()) {
            String key = entry.getKey();
            List<ApplicationOptionDTO> aos =
                    new ArrayList<ApplicationOptionDTO>();
            for (String aoOid : entry.getValue()) {
                ApplicationOptionDTO ao = koulutusinformaatioService.getApplicationOption(aoOid, lang);
                ao = ensureAddress(ao);
                if (!addressAlreadyAdded(aos, ao)) {
                    aos.add(ao);
                }
            }
            higherEdAttachments.put(key, aos);
        }

        // TODO Seriously, this shouldn't be hardcoded.
        Calendar deadlineCal = GregorianCalendar.getInstance();
        deadlineCal.set(Calendar.YEAR, 2014);
        deadlineCal.set(Calendar.MONTH, Calendar.OCTOBER);
        deadlineCal.set(Calendar.DATE, 6);
        deadlineCal.set(Calendar.HOUR_OF_DAY, 15);
        deadlineCal.set(Calendar.MINUTE, 0);
        deadlineCal.set(Calendar.SECOND, 0);
        Date deadline = deadlineCal.getTime();

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
                                .setDeadline(deadline)
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

}
