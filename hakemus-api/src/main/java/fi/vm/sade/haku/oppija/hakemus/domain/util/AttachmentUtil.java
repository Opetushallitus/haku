package fi.vm.sade.haku.oppija.hakemus.domain.util;

import fi.vm.sade.haku.oppija.hakemus.domain.*;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationOptionAttachmentRequest;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.SimpleAddress;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.I18nBundle;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.koulutusinformaatio.domain.dto.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class AttachmentUtil {

    public static final String GENERAL_DELIVERY_NOTE = "lomake.tulostus.liite.deadline.tarkista";

    public static List<ApplicationAttachment> resolveAttachments(Application application) {
        List<ApplicationAttachmentRequest> attachmentRequests = application.getAttachmentRequests();
        List<ApplicationAttachment> attachments = new ArrayList<ApplicationAttachment>(attachmentRequests.size());
        for (ApplicationAttachmentRequest attachmentRequest : attachmentRequests) {
            attachments.add(attachmentRequest.getApplicationAttachment());
        }
        return attachments;
    }

    public static List<ApplicationAttachmentRequest> resolveAttachmentRequests(
      final ApplicationSystem applicationSystem,
      final Application application,
      final KoulutusinformaatioService koulutusinformaatioService,
      final I18nBundle i18nBundle) {

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
        return resolveAttachmentRequests(applicationSystem, application, koulutusinformaatioService, lang, i18nBundle);
    }

    private static List<ApplicationAttachmentRequest> resolveAttachmentRequests(
      final ApplicationSystem applicationSystem,
      final Application application,
      final KoulutusinformaatioService koulutusinformaatioService,
      final String lang,
      final I18nBundle i18nBundle) {
        List<ApplicationAttachmentRequest> attachments = new ArrayList<ApplicationAttachmentRequest>();
        attachments = addApplicationOptionAttachments(attachments, application, koulutusinformaatioService, lang, i18nBundle);
        attachments = addDiscreationaryAttachments(attachments, application, koulutusinformaatioService, lang,
          i18nBundle);
        attachments = addHigherEdAttachments(attachments, application, koulutusinformaatioService, lang, i18nBundle);
        attachments = addAmkOpeAttachments(attachments, application, koulutusinformaatioService, lang, i18nBundle);
        attachments = addApplicationOptionAttachmentRequestsFromForm(attachments, application, applicationSystem, i18nBundle);

        return attachments;
    }

    private static List<ApplicationAttachmentRequest> addApplicationOptionAttachmentRequestsFromForm(
      List<ApplicationAttachmentRequest> attachments, Application application,
      ApplicationSystem applicationSystem, I18nBundle i18nBundle) {
        if (applicationSystem.getApplicationOptionAttachmentRequests() == null) {
            return attachments;
        }
        for (ApplicationOptionAttachmentRequest attachmentRequest : applicationSystem.getApplicationOptionAttachmentRequests()) {
            if (attachmentRequest.include(application.getVastauksetMerged())) {
                SimpleAddress address = attachmentRequest.getDeliveryAddress();
                ApplicationAttachmentRequestBuilder attachmentRequestBuilder = ApplicationAttachmentRequestBuilder.start();

                if (attachmentRequest.isGroupOption())
                    attachmentRequestBuilder.setPreferenceAoGroupId(attachmentRequest.getApplicationOptionId());
                else
                    attachmentRequestBuilder.setPreferenceAoId(attachmentRequest.getApplicationOptionId());

                Date deadline = attachmentRequest.getDeliveryDue();

                ApplicationAttachmentBuilder attachmentBuilder = ApplicationAttachmentBuilder.start()
                        .setHeader(attachmentRequest.getHeader())
                        .setDescription(attachmentRequest.getDescription())
                        .setDeadline(deadline)
                        .setAddress(AddressBuilder.start()
                                .setRecipient(address.getRecipient())
                                .setStreetAddress(address.getStreet())
                                .setPostalCode(address.getPostCode())
                                .setPostOffice(address.getPostOffice())
                                .build());

                if (deadline == null) {
                    attachmentBuilder.setDeliveryNote(i18nBundle.get(GENERAL_DELIVERY_NOTE));
                }

                attachments.add(attachmentRequestBuilder
                        .setApplicationAttachment(attachmentBuilder.build())
                        .build());
            }
        }
        return attachments;
    }

    private static List<ApplicationAttachmentRequest> addApplicationOptionAttachments(
      List<ApplicationAttachmentRequest> attachments, Application application,
      KoulutusinformaatioService koulutusinformaatioService, String lang, I18nBundle i18nBundle) {
        for (String aoOid : ApplicationUtil.getApplicationOptionAttachmentAOIds(application)) {
            ApplicationOptionDTO ao = koulutusinformaatioService.getApplicationOption(aoOid, lang);
            String name = null;
            if (ao.getProvider().getApplicationOffice() != null &&
              ao.getProvider().getApplicationOffice().getPostalAddress() != null) {
                name = ao.getProvider().getApplicationOffice().getName();
            } else {
                name = ao.getProvider().getName();
            }
            for (ApplicationOptionAttachmentDTO attachmentDTO : ao.getAttachments()) {
                if (attachmentDTO.isUsedInApplicationForm()) {
                    String descriptionText = attachmentDTO.getDescreption();
                    I18nText description = null;
                    if (isNotBlank(descriptionText)) {
                        description = createI18NAsIs(descriptionText);
                    } else {
                        description = createI18NAsIs("");
                    }

                    Date deadline = attachmentDTO.getDueDate();

                    ApplicationAttachmentBuilder attachmentBuilder = ApplicationAttachmentBuilder.start()
                            .setName(createI18NAsIs(attachmentDTO.getType()))
                            .setDescription(description)
                            .setDeadline(deadline)
                            .setAddress(getAddress(name, attachmentDTO.getAddress()));

                    if (deadline == null) {
                        attachmentBuilder.setDeliveryNote(i18nBundle.get(GENERAL_DELIVERY_NOTE));
                    }

                    attachments.add(ApplicationAttachmentRequestBuilder.start()
                            .setPreferenceAoId(ao.getId())
                            .setApplicationAttachment(attachmentBuilder.build())
                            .build());
                }
            }
        }
        return attachments;
    }

    private static List<ApplicationAttachmentRequest> addDiscreationaryAttachments(
      final List<ApplicationAttachmentRequest> attachments,
      final Application application,
      final KoulutusinformaatioService koulutusinformaatioService,
      final String lang,
      final I18nBundle i18nBundle) {

        for (String aoOid : ApplicationUtil.getDiscretionaryAttachmentAOIds(application)) {
            ApplicationOptionDTO ao = koulutusinformaatioService.getApplicationOption(aoOid, lang);
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

            Date deadline = ao.getAttachmentDeliveryDeadline();
            ApplicationAttachmentBuilder attachmentBuilder = ApplicationAttachmentBuilder.start()
                    .setName(i18nBundle.get("form.valmis.liitteet.harkinnanvaraisuus"))
                    .setDeliveryNote(i18nBundle.get("lomake.tulostus.liite.deadline.tarkista"))
                    .setAddress(getAddress(ao));
            if (discreationaryReason != null) {
                attachmentBuilder.setDescription(i18nBundle.get("form.valmis.liitteet.harkinnanvaraisuus."
                  + discreationaryReason));
            }
            if (deadline == null) {
                attachmentBuilder.setDeliveryNote(i18nBundle.get(GENERAL_DELIVERY_NOTE));
            }

            attachments.add(ApplicationAttachmentRequestBuilder.start()
              .setPreferenceAoId(aoOid)
              .setApplicationAttachment(attachmentBuilder.build())
              .build());
        }
        return attachments;
    }

    private static Address getAddress(String recipient, AddressDTO addressDTO) {
        if (null == addressDTO)
            return null;
        return AddressBuilder.start()
          .setRecipient(recipient)
          .setStreetAddress(addressDTO.getStreetAddress())
          .setStreetAddress2(addressDTO.getStreetAddress2())
          .setPostalCode(addressDTO.getPostalCode())
          .setPostOffice(addressDTO.getPostOffice())
          .build();
    }

    private static Address getAddress(ApplicationOptionDTO ao) {
        AddressDTO addressDTO = ao.getAttachmentDeliveryAddress();
        if (addressDTO == null && ao.getProvider() != null) {
            addressDTO = ao.getProvider().getPostalAddress();
        }
        if (addressDTO == null) {
            return null;
        }
        return AddressBuilder.start()
          .setRecipient(ao.getProvider().getName() + " " + ao.getName())
          .setStreetAddress(addressDTO.getStreetAddress())
          .setStreetAddress2(addressDTO.getStreetAddress2())
          .setPostalCode(addressDTO.getPostalCode())
          .setPostOffice(addressDTO.getPostOffice())
          .build();
    }

    private static List<ApplicationAttachmentRequest> addHigherEdAttachments(
      final List<ApplicationAttachmentRequest> attachments,
      final Application application,
      final KoulutusinformaatioService koulutusinformaatioService,
      final String lang,
      final I18nBundle i18nBundle) {

        Map<String, List<AttachmentAddressInfo>> higherEdAttachments = getAddresses(ApplicationUtil.getHigherEdAttachmentAOIds(application), koulutusinformaatioService, lang, true);
        Date deadline = null;
        attachments.addAll(getHigherEdAttachments(higherEdAttachments, deadline, i18nBundle));
        return attachments;
    }

    private static List<ApplicationAttachmentRequest> addAmkOpeAttachments(
            final List<ApplicationAttachmentRequest> attachments, final Application application,
            final KoulutusinformaatioService koulutusinformaatioService, final String lang,
            final I18nBundle i18nBundle) {

        Map<String, List<AttachmentAddressInfo>> higherEdAttachments = getAddresses(ApplicationUtil.getAmkOpeAttachments(application), koulutusinformaatioService, lang, false);

        Calendar deadlineCal = GregorianCalendar.getInstance();
        deadlineCal.set(Calendar.YEAR, 2015);
        deadlineCal.set(Calendar.MONTH, GregorianCalendar.FEBRUARY);
        deadlineCal.set(Calendar.DAY_OF_MONTH, 3);
        deadlineCal.set(Calendar.HOUR_OF_DAY, 15);
        deadlineCal.set(Calendar.MINUTE, 0);
        deadlineCal.set(Calendar.SECOND, 0);

        Date deadline = deadlineCal.getTime();

        attachments.addAll(getHigherEdAttachments(higherEdAttachments, deadline, i18nBundle));
        return attachments;
    }

    private static List<ApplicationAttachmentRequest> getHigherEdAttachments(
            final Map<String, List<AttachmentAddressInfo>> higherEdAttachments,
            final Date deadline,
            final I18nBundle i18nBundle) {

        List<ApplicationAttachmentRequest> attachments = new ArrayList<>();
        for (Map.Entry<String, List<AttachmentAddressInfo>> entry : higherEdAttachments.entrySet()) {
            String attachmentType = entry.getKey();
            for (AttachmentAddressInfo address : entry.getValue()) {

                ApplicationAttachmentBuilder attachmentBuilder = ApplicationAttachmentBuilder.start()
                        .setName(address.attachmentName)
                        .setDescription(i18nBundle.get(attachmentType))
                        .setDeadline(deadline)
                        .setAddress(getAddress(address.recipientName, address.addressDTO));
                if (deadline == null) {
                    attachmentBuilder.setDeliveryNote(i18nBundle.get(GENERAL_DELIVERY_NOTE));
                }

                attachments.add(ApplicationAttachmentRequestBuilder.start()
                        .setPreferenceAoId(address.attachmentOriginatorAoId)
                        .setPreferenceAoGroupId(address.attachmentOriginatorGroupId)
                        .setApplicationAttachment(attachmentBuilder.build())
                        .build());
            }
        }
        return attachments;
    }

    private static Map<String, List<AttachmentAddressInfo>> getAddresses(
      final Map<String, List<String>> higherEdAttachmentAOIds,
      final KoulutusinformaatioService koulutusinformaatioService,
      final String lang,
      final boolean useGroupAddresses) {
        Map<String, List<AttachmentAddressInfo>> applicationOptions = new HashMap<>();
        new HashMap<String, List<AttachmentAddressInfo>>();
        for (Map.Entry<String, List<String>> entry : higherEdAttachmentAOIds.entrySet()) {
            String key = entry.getKey();
            List<AttachmentAddressInfo> addresses = new ArrayList<>();
            for (String aoOid : entry.getValue()) {
                ApplicationOptionDTO ao = koulutusinformaatioService.getApplicationOption(aoOid, lang);
                AttachmentAddressInfo address = useGroupAddresses ? getAttachmentGroupAddressInfo(ao) : getAttachmentAddressInfo(ao);
                if (!addressAlreadyAdded(addresses, address)) {
                    addresses.add(address);
                }
            }
            applicationOptions.put(key, addresses);
        }
        return applicationOptions;
    }

    private static AttachmentAddressInfo getAttachmentGroupAddressInfo(ApplicationOptionDTO ao) {
        AttachmentAddressInfo aoAddress = getAttachmentAddressInfo(ao);
        for (OrganizationGroupDTO organizationGroup : ao.getOrganizationGroups()) {
            // TODO get group address from form config
            if (organizationGroup.getUsageGroups().contains(OppijaConstants.OPTION_ATTACHMENT_GROUP_TYPE)){
                return new AttachmentAddressInfo(
                        aoAddress.attachmentName,
                        aoAddress.recipientName,
                        aoAddress.addressDTO,
                        AttachmentAddressInfo.OriginatorType.group,
                        organizationGroup.getOid()
                );

            }
        }
        return aoAddress;
    }

    private static AttachmentAddressInfo getAttachmentAddressInfo(ApplicationOptionDTO ao) {
        LearningOpportunityProviderDTO provider = ao.getProvider();
        String recipientName = provider.getName();
        AddressDTO address = provider.getPostalAddress();
        if (provider.getApplicationOffice() != null && provider.getApplicationOffice().getPostalAddress() != null) {
            recipientName = provider.getApplicationOffice().getName();
            address = provider.getApplicationOffice().getPostalAddress();
        }
        return new AttachmentAddressInfo(
            createI18NAsIs(StringUtil.safeToString(provider.getName())),
            recipientName,
            address,
            AttachmentAddressInfo.OriginatorType.applicationOption,
            ao.getId()
        );
    }

    private static boolean addressAlreadyAdded(List<AttachmentAddressInfo> addresses, AttachmentAddressInfo address) {
        for (AttachmentAddressInfo other : addresses) {
            if (StringUtils.equals(address.attachmentOriginatorAoId, other.attachmentOriginatorAoId) &&
                StringUtils.equals(address.attachmentOriginatorGroupId, other.attachmentOriginatorGroupId)) {
                return true;
            }
        }
        return false;
    }

}
