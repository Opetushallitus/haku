package fi.vm.sade.haku.oppija.hakemus.domain.util;

import com.google.common.base.Function;
import com.google.common.collect.*;
import fi.vm.sade.haku.oppija.hakemus.domain.*;
import fi.vm.sade.haku.oppija.hakemus.domain.HigherEdBaseEducationAttachmentInfo.OriginatorType;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationOptionAttachmentRequest;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.AttachmentGroupAddress;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.SimpleAddress;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.I18nBundle;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.koulutusinformaatio.domain.dto.*;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class AttachmentUtil {

    public static final String GENERAL_DELIVERY_NOTE = "lomake.tulostus.liite.deadline.tarkista";
    public static final String GENERAL_DEADLINE_NOTE = "lomake.tulostus.liite.deadline.ohje";
    public static final String YO_POHJAKOULUTUSKOODI = "pohjakoulutuskklomake_yosuomi";
    public static final Set<String> KV_YO_POHJAKOULUTUSKOODI = new HashSet<String>() {{
        add("pohjakoulutuskklomake_yokvsuomi");
        add("pohjakoulutuskklomake_pohjakoulutusyoulkomainen");
    }};
    public static final HashMap<String, String> POHJAKOULUTUSKOODI_TO_I18N_KEY = new HashMap<String, String>() {{
        put("pohjakoulutuskklomake_amsuomi", "form.valmis.todistus.am");
        put("pohjakoulutuskklomake_pohjakoulutusamt", "form.valmis.todistus.amt");
        put("pohjakoulutuskklomake_pohjakoulutusavoin", "form.valmis.todistus.avoin");
        put("pohjakoulutuskklomake_pohjakoulutuskk", "form.valmis.todistus.kk");
        put("pohjakoulutuskklomake_pohjakoulutuskkulk", "form.valmis.todistus.kk_ulk");
        put("pohjakoulutuskklomake_pohjakoulutusmuu", "form.valmis.todistus.muu");
        put("pohjakoulutuskklomake_muuulk", "form.valmis.todistus.ulk");
        put("pohjakoulutuskklomake_yosuomi", "form.valmis.todistus.yo");
        put("pohjakoulutuskklomake_pohjakoulutusyoammatillinen", "form.valmis.todistus.yo_am");
        put("pohjakoulutuskklomake_yokvsuomi", "form.valmis.todistus.yo_kv");
        put("pohjakoulutuskklomake_pohjakoulutusyoulkomainen", "form.valmis.todistus.yo_ulk");
    }};
    public static final HashMap<String, String> POHJAKOULUTUSKOODI_TO_FORM_ID = new HashMap<String, String>() {{
        put("pohjakoulutuskklomake_amsuomi", "pohjakoulutus_am");
        put("pohjakoulutuskklomake_pohjakoulutusamt", "pohjakoulutus_amt");
        put("pohjakoulutuskklomake_pohjakoulutusavoin", "pohjakoulutus_avoin");
        put("pohjakoulutuskklomake_pohjakoulutuskk", "pohjakoulutus_kk");
        put("pohjakoulutuskklomake_pohjakoulutuskkulk", "pohjakoulutus_kk_ulk");
        put("pohjakoulutuskklomake_pohjakoulutusmuu", "pohjakoulutus_muu");
        put("pohjakoulutuskklomake_muuulk", "pohjakoulutus_ulk");
        put("pohjakoulutuskklomake_yosuomi", "pohjakoulutus_yo");
        put("pohjakoulutuskklomake_pohjakoulutusyoammatillinen", "pohjakoulutus_yo_ammatillinen");
        put("pohjakoulutuskklomake_yokvsuomi", "pohjakoulutus_yo_kansainvalinen_suomessa");
        put("pohjakoulutuskklomake_pohjakoulutusyoulkomainen", "pohjakoulutus_yo_ulkomainen");
    }};

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
        attachments = addDiscreationaryAttachments(attachments, application, koulutusinformaatioService, lang, i18nBundle);
        if(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(applicationSystem.getKohdejoukkoUri())) {
            attachments = addHigherEdAttachments(applicationSystem, attachments, application, koulutusinformaatioService, lang, i18nBundle);
            attachments = addAmkOpeAttachments(applicationSystem, attachments, application, koulutusinformaatioService, lang, i18nBundle);
        }
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
            if (ao.getApplicationOffice() != null) {
                name = ao.getApplicationOffice().getName();
            } else if (ao.getProvider().getApplicationOffice() != null
                    && ao.getProvider().getApplicationOffice().getPostalAddress() != null) {
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

                    // Jos tarjontaan on merkitty, että liiteet voidaan toimittaa sähköisesti
                    String email = attachmentDTO.getEmailAddr();

                    ApplicationAttachmentBuilder attachmentBuilder = ApplicationAttachmentBuilder.start()
                            .setName(createI18NAsIs(attachmentDTO.getType()))
                            .setDescription(description)
                            .setDeadline(deadline)
                            .setEmailAddress(email)
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
          .setRecipient(StringUtil.safeToString(recipient))
          .setStreetAddress(addressDTO.getStreetAddress())
          .setStreetAddress2(addressDTO.getStreetAddress2())
          .setPostalCode(addressDTO.getPostalCode())
          .setPostOffice(addressDTO.getPostOffice())
          .build();
    }

    private static Address getAddress(SimpleAddress address) {
        if (null == address)
            return null;
        return AddressBuilder.start()
                .setRecipient(StringUtil.safeToString(address.getRecipient()))
                .setStreetAddress(address.getStreet())
                .setPostalCode(address.getPostCode())
                .setPostOffice(address.getPostOffice())
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

    private static Map<String, List<ApplicationOptionDTO>> fetchAOs(Map<String, List<String>> attachmentsToAOoids,
                                                                    final KoulutusinformaatioService koulutusinformaatioService,
                                                                    final String lang) {
        final Map<String, ApplicationOptionDTO> cache = new HashMap<>();
        return Maps.transformValues(attachmentsToAOoids, new Function<List<String>, List<ApplicationOptionDTO>>() {
            public List<ApplicationOptionDTO> apply(List<String> oids) {
                return Lists.transform(oids, new Function<String, ApplicationOptionDTO>() {
                    public ApplicationOptionDTO apply(String oid) {
                        if (!cache.containsKey(oid)) {
                            cache.put(oid, koulutusinformaatioService.getApplicationOption(oid, lang));
                        }
                        return cache.get(oid);
                    }
                });
            }
        });
    }

    private static boolean liitepyynto(Application application, ApplicationOptionDTO ao, String pohjakoulutuskoodi) {
        if (!ApplicationUtil.hasBaseEducation(application, POHJAKOULUTUSKOODI_TO_FORM_ID.get(pohjakoulutuskoodi))) {
            return false;
        }
        if (YO_POHJAKOULUTUSKOODI.equals(pohjakoulutuskoodi)) {
            return ApplicationUtil.yoNeeded(application);
        }
        if (ao.isJosYoEiMuitaLiitepyyntoja()) {
            return (KV_YO_POHJAKOULUTUSKOODI.contains(pohjakoulutuskoodi) || !ApplicationUtil.hasBaseEducationYoOrKvYo(application));
        }
        return true;
    }

    public static Map<String, List<ApplicationOptionDTO>> pohjakoulutusliitepyynnot(Application application, List<ApplicationOptionDTO> aos) {
        Map<String, List<ApplicationOptionDTO>> liitepyynnot = new HashMap<>();
        for (ApplicationOptionDTO ao : aos) {
            if (ao.getPohjakoulutusLiitteet() != null) {
                for (String pohjakoulutuskoodi : ao.getPohjakoulutusLiitteet()) {
                    if (liitepyynto(application, ao, pohjakoulutuskoodi)) {
                        String i18nKey = POHJAKOULUTUSKOODI_TO_I18N_KEY.get(pohjakoulutuskoodi);
                        if (!liitepyynnot.containsKey(i18nKey)) {
                            liitepyynnot.put(i18nKey, new ArrayList<ApplicationOptionDTO>());
                        }
                        liitepyynnot.get(i18nKey).add(ao);
                    }
                }
            }
        }
        return liitepyynnot;
    }

    private static List<ApplicationAttachmentRequest> addHigherEdAttachments(
        final ApplicationSystem applicationSystem,
        final List<ApplicationAttachmentRequest> attachments,
        final Application application,
        final KoulutusinformaatioService koulutusinformaatioService,
        final String lang,
        final I18nBundle i18nBundle
    ) {
        Date deadline = null;
        Map<String, List<ApplicationOptionDTO>> higherEdAttachmentAOs = true ?
                pohjakoulutusliitepyynnot(application, koulutusinformaatioService.getApplicationOptions(ApplicationUtil.getPreferenceAoIds(application), lang)) :
                fetchAOs(ApplicationUtil.getHigherEdAttachmentAOIds(application), koulutusinformaatioService, lang);
        Map<String, List<HigherEdBaseEducationAttachmentInfo>> higherEdAttachments = getAddresses(applicationSystem.getAttachmentGroupAddresses(), higherEdAttachmentAOs, deadline);
        attachments.addAll(getHigherEdAttachments(higherEdAttachments, i18nBundle));
        return attachments;
    }

    private static List<ApplicationAttachmentRequest> addAmkOpeAttachments(
            final ApplicationSystem applicationSystem,
            final List<ApplicationAttachmentRequest> attachments, final Application application,
            final KoulutusinformaatioService koulutusinformaatioService, final String lang,
            final I18nBundle i18nBundle) {

        Calendar deadlineCal = GregorianCalendar.getInstance();
        deadlineCal.set(Calendar.YEAR, 2015);
        deadlineCal.set(Calendar.MONTH, GregorianCalendar.FEBRUARY);
        deadlineCal.set(Calendar.DAY_OF_MONTH, 3);
        deadlineCal.set(Calendar.HOUR_OF_DAY, 15);
        deadlineCal.set(Calendar.MINUTE, 0);
        deadlineCal.set(Calendar.SECOND, 0);
        Date deadline = deadlineCal.getTime();

        Map<String, List<ApplicationOptionDTO>> amkOpeAttachmentAOs = fetchAOs(ApplicationUtil.getAmkOpeAttachments(application), koulutusinformaatioService, lang);
        Map<String, List<HigherEdBaseEducationAttachmentInfo>> higherEdAttachments = getAddresses(applicationSystem.getAttachmentGroupAddresses(), amkOpeAttachmentAOs, deadline);

        attachments.addAll(getHigherEdAttachments(higherEdAttachments, i18nBundle));
        return attachments;
    }

    private static List<ApplicationAttachmentRequest> getHigherEdAttachments(
            final Map<String, List<HigherEdBaseEducationAttachmentInfo>> higherEdAttachments,
            final I18nBundle i18nBundle) {

        List<ApplicationAttachmentRequest> attachments = new ArrayList<>();
        for (Map.Entry<String, List<HigherEdBaseEducationAttachmentInfo>> entry : higherEdAttachments.entrySet()) {
            String attachmentType = entry.getKey();
            for (HigherEdBaseEducationAttachmentInfo address : entry.getValue()) {

                ApplicationAttachmentBuilder attachmentBuilder = ApplicationAttachmentBuilder.start()
                        .setName(i18nBundle.get(attachmentType))
                        .setDescription(address.description)
                        .setDeadline(address.deadline)
                        .setAddress(address.address);


                if (address.deadline == null) {
                    attachmentBuilder.setDeliveryNote(i18nBundle.get(GENERAL_DELIVERY_NOTE));
                } else {
                    attachmentBuilder.setDeliveryNote(i18nBundle.get(GENERAL_DEADLINE_NOTE));
                }

                attachments.add(ApplicationAttachmentRequestBuilder.start()
                        .setId(attachmentType + '_' + address.originatorType + '_' + address.originatorId)
                        .setPreferenceAoId(address.originatorType == OriginatorType.applicationOption ? address.originatorId : null)
                        .setPreferenceAoGroupId(address.originatorType == OriginatorType.group ? address.originatorId : null)
                        .setApplicationAttachment(attachmentBuilder.build())
                        .build());
            }
        }
        return attachments;
    }

    private static Map<String, List<HigherEdBaseEducationAttachmentInfo>> getAddresses(
      final List<AttachmentGroupAddress> attachmentGroupAddresses,
      final Map<String, List<ApplicationOptionDTO>> attachmentAOs,
      final Date defaultDeadline) {
        return Maps.transformValues(attachmentAOs, new Function<List<ApplicationOptionDTO>, List<HigherEdBaseEducationAttachmentInfo>>() {
            @Override
            public List<HigherEdBaseEducationAttachmentInfo> apply(List<ApplicationOptionDTO> applicationOptionDTOs) {
                List<HigherEdBaseEducationAttachmentInfo> addresses = new ArrayList<>();
                for (ApplicationOptionDTO ao : applicationOptionDTOs) {
                    HigherEdBaseEducationAttachmentInfo address = getAttachmentGroupAddressInfo(attachmentGroupAddresses, ao, defaultDeadline);
                    if (!addressAlreadyAdded(addresses, address)) {
                        addresses.add(address);
                    }
                }
                return addresses;
            }
        });
    }

    private static HigherEdBaseEducationAttachmentInfo getAttachmentGroupAddressInfo(List<AttachmentGroupAddress> attachmentGroupAddresses, ApplicationOptionDTO ao, Date defaultDeadline) {
        HigherEdBaseEducationAttachmentInfo aoAddress = getAttachmentAddressInfo(ao, defaultDeadline);
        for (OrganizationGroupDTO organizationGroup : ao.getOrganizationGroups()) {
            for (AttachmentGroupAddress groupAddress: attachmentGroupAddresses) {
                if (organizationGroup.getOid().equals(groupAddress.getGroupId())) {
                    return new HigherEdBaseEducationAttachmentInfo(
                            chooseAddress(groupAddress, aoAddress),
                            OriginatorType.group,
                            organizationGroup.getOid(),
                            null,
                            groupAddress.isUseFirstAoAddress() ? createI18NAsIs(ao.getProvider().getName()) : null,
                            groupAddress.getDeliveryDue() == null ? defaultDeadline : groupAddress.getDeliveryDue()
                    );
                }
            }
        }
        return aoAddress;
    }

    private static Address chooseAddress(AttachmentGroupAddress groupAddress, HigherEdBaseEducationAttachmentInfo aoAddress) {
        if(groupAddress.isUseFirstAoAddress()) {
            return aoAddress.address;
        }
        return getAddress(groupAddress.getDeliveryAddress());
    }

    private static HigherEdBaseEducationAttachmentInfo getAttachmentAddressInfo(ApplicationOptionDTO ao, Date deadline) {
        LearningOpportunityProviderDTO provider = ao.getProvider();
        String recipientName = provider.getName();
        AddressDTO address = provider.getPostalAddress();
        if (ao.getApplicationOffice() != null) {
            recipientName = ao.getApplicationOffice().getName();
            address = ao.getApplicationOffice().getPostalAddress();
        } else if (provider.getApplicationOffice() != null && provider.getApplicationOffice().getPostalAddress() != null) {
            if(StringUtils.isNotEmpty(provider.getApplicationOffice().getName())) {
                recipientName = provider.getApplicationOffice().getName();
            }
            address = provider.getApplicationOffice().getPostalAddress();
        }
        return new HigherEdBaseEducationAttachmentInfo(
            getAddress(recipientName, address),
            OriginatorType.applicationOption,
            ao.getId(),
            provider.getId(),
            createI18NAsIs(provider.getName()),
            deadline
        );
    }

    private static boolean addressAlreadyAdded(List<HigherEdBaseEducationAttachmentInfo> addresses, HigherEdBaseEducationAttachmentInfo address) {
        for (HigherEdBaseEducationAttachmentInfo other : addresses) {
            if (StringUtils.equals(address.originatorId, other.originatorId)) {
                return true;
            }
            if (address.originatorType == OriginatorType.applicationOption
                && other.originatorType == OriginatorType.applicationOption
                && StringUtils.equals(address.providerId, other.providerId)) {
                return true;
            }
            if (address.originatorType == OriginatorType.applicationOption
                && other.originatorType == OriginatorType.applicationOption
                && address.address.equals(other.address)) {
                return true;
            }
        }
        return false;
    }

}
