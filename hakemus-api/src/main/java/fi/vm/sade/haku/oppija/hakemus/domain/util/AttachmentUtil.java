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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class AttachmentUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentUtil.class);

    private enum Pohjakoulutusliite {
        AMMATTI("pohjakoulutuskklomake_amsuomi", "form.valmis.todistus.am", "pohjakoulutus_am"),
        AMMATILLINEN("pohjakoulutuskklomake_pohjakoulutusamt", "form.valmis.todistus.amt", "pohjakoulutus_amt"),
        AVOIN("pohjakoulutuskklomake_pohjakoulutusavoin", "form.valmis.todistus.avoin", "pohjakoulutus_avoin"),
        KK("pohjakoulutuskklomake_pohjakoulutuskk", "form.valmis.todistus.kk", "pohjakoulutus_kk"),
        KK_ULK("pohjakoulutuskklomake_pohjakoulutuskkulk", "form.valmis.todistus.kk_ulk", "pohjakoulutus_kk_ulk"),
        MUU("pohjakoulutuskklomake_pohjakoulutusmuu", "form.valmis.todistus.muu", "pohjakoulutus_muu"),
        MUU_ULK("pohjakoulutuskklomake_muuulk", "form.valmis.todistus.ulk", "pohjakoulutus_ulk"),
        YO("pohjakoulutuskklomake_yosuomi", "form.valmis.todistus.yo", "pohjakoulutus_yo"),
        LUKIO("pohjakoulutuskklomake_pohjakoulutuslk", "form.valmis.todistus.lukio", "pohjakoulutus_yo"),
        YO_AMMATILLINEN("pohjakoulutuskklomake_pohjakoulutusyoammatillinen", "form.valmis.todistus.yo_am", "pohjakoulutus_yo_ammatillinen"),
        KV_YO("pohjakoulutuskklomake_yokvsuomi", "form.valmis.todistus.yo_kv", "pohjakoulutus_yo_kansainvalinen_suomessa"),
        KV_YO_ULK("pohjakoulutuskklomake_pohjakoulutusyoulkomainen", "form.valmis.todistus.yo_ulk", "pohjakoulutus_yo_ulkomainen");

        public final String koodiUri;
        public final String i18nKey;
        public final String formId;

        Pohjakoulutusliite(String koodiUri, String i18nKey, String formId) {
            this.koodiUri = koodiUri;
            this.i18nKey = i18nKey;
            this.formId = formId;
        }

        public static Pohjakoulutusliite byKoodiUri(String koodiUri) {
            for (Pohjakoulutusliite p : Pohjakoulutusliite.values()) {
                if (p.koodiUri.equals(koodiUri)) {
                    return p;
                }
            }
            throw new EnumConstantNotPresentException(Pohjakoulutusliite.class, koodiUri);
        }
    }

    public static final String GENERAL_DELIVERY_NOTE = "lomake.tulostus.liite.deadline.tarkista";
    public static final String GENERAL_DEADLINE_NOTE = "lomake.tulostus.liite.deadline.ohje";

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
        if(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(applicationSystem.getKohdejoukkoUri())) {
            attachments = addHigherEdAttachments(applicationSystem, attachments, application, koulutusinformaatioService, lang, i18nBundle);
            attachments = addAmkOpeAttachments(applicationSystem, attachments, application, koulutusinformaatioService, lang, i18nBundle);
        }
        attachments = addApplicationOptionAttachments(attachments, application, koulutusinformaatioService, lang, i18nBundle);
        attachments = addDiscreationaryAttachments(attachments, application, koulutusinformaatioService, lang, i18nBundle);
        attachments = addToimitaKopioSuorittamastasiKielitutkinnostaEnsimmaiseenAmmatillisenKoulutuksenHakutoiveeseenHakuajanLoppuunMennessaAttachments(attachments, application, koulutusinformaatioService, lang, i18nBundle);
        attachments = addApplicationOptionAttachmentRequestsFromForm(attachments, application, applicationSystem, i18nBundle, koulutusinformaatioService, lang);

        return attachments;
    }

    private static List<ApplicationAttachmentRequest> addApplicationOptionAttachmentRequestsFromForm(
      List<ApplicationAttachmentRequest> attachments, Application application,
      ApplicationSystem applicationSystem, I18nBundle i18nBundle,
      KoulutusinformaatioService koulutusinformaatioService, String lang) {
        if (applicationSystem.getApplicationOptionAttachmentRequests() == null) {
            return attachments;
        }

        for (ApplicationOptionAttachmentRequest attachmentRequest : applicationSystem.getApplicationOptionAttachmentRequests()) {
            if (attachmentRequest.include(application.getVastauksetMerged())) {
                SimpleAddress address = attachmentRequest.getDeliveryAddress();
                ApplicationAttachmentRequestBuilder attachmentRequestBuilder = ApplicationAttachmentRequestBuilder.start();

                Date deadline = attachmentRequest.getDeliveryDue();

                ApplicationAttachmentBuilder attachmentBuilder = ApplicationAttachmentBuilder.start()
                        .setHeader(attachmentRequest.getHeader())
                        .setDescription(attachmentRequest.getDescription())
                        .setDeadline(deadline);

                Address addressObj = null;
                if (attachmentRequest.isGroupOption()) {
                    attachmentRequestBuilder.setPreferenceAoGroupId(attachmentRequest.getApplicationOptionId());

                    if (attachmentRequest.getOverrideAddress() != null && attachmentRequest.getOverrideAddress()) {
                        // Override specified address, use first ao/provider address of the group instead
                        List<ApplicationOptionDTO> prefAOs = koulutusinformaatioService.getApplicationOptions(ApplicationUtil.getPreferenceAoIds(application), lang);
                        addressObj = parseFirstGroupAddress(attachmentRequest.getApplicationOptionId(), prefAOs);
                    }
                } else {
                    attachmentRequestBuilder.setPreferenceAoId(attachmentRequest.getApplicationOptionId());

                    if (attachmentRequest.getOverrideAddress() != null && attachmentRequest.getOverrideAddress()) {
                        // Override specified address, use ao/provider address instead
                        ApplicationOptionDTO ao = koulutusinformaatioService.getApplicationOption(attachmentRequest.getApplicationOptionId(), lang);
                        addressObj = selectPreferredAddress(ao);
                    }
                }

                if(addressObj == null) {
                    addressObj = AddressBuilder.start()
                            .setRecipient(address.getRecipient())
                            .setStreetAddress(address.getStreet())
                            .setPostalCode(address.getPostCode())
                            .setPostOffice(address.getPostOffice())
                            .build();
                }

                attachmentBuilder.setAddress(addressObj);

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

    private static Address parseFirstGroupAddress(String groupId, List<ApplicationOptionDTO> prefAOs) {
        LOGGER.debug("Searching attachment address for orgGroupId:" + groupId);
        for (ApplicationOptionDTO aolistItem : prefAOs) {
            for (OrganizationGroupDTO aoOrgGroupInList : aolistItem.getOrganizationGroups()) {
                if (groupId.equals(aoOrgGroupInList.getOid())) {
                    LOGGER.debug("Using attachment address for orgGroupId:" + groupId + " from first ao id:" + aolistItem.getId());
                    return selectPreferredAddress(aolistItem);
                }
            }
        }
        return null;
    }

    private static List<ApplicationAttachmentRequest> addApplicationOptionAttachments(
            List<ApplicationAttachmentRequest> attachments, Application application,
            KoulutusinformaatioService koulutusinformaatioService, String lang, I18nBundle i18nBundle) {
        for (String aoOid : ApplicationUtil.getPreferenceAoIds(application)) {
            ApplicationOptionDTO ao = koulutusinformaatioService.getApplicationOption(aoOid, lang);
            if(ao.getAttachments() == null){
                continue;
            }

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
    private static List<String> resolveKielitutkintoKeys() {
        List<String> keys = new ArrayList<>();
        for(String lang : OppijaConstants.LANGUAGES) {
            keys.add(String.format(OppijaConstants.YLEINEN_KIELITUTKINTO, lang));
            keys.add(String.format(OppijaConstants.VALTIONHALLINNON_KIELITUTKINTO, lang));
        }
        return keys;
    }
    private static boolean anyKeyTrue(List<String> keys, Map<String, String> answers) {
        for(String key: keys) {
            if(Boolean.parseBoolean(answers.get(key))) {
                return true;
            }
        }
        return false;
    }
    private static List<ApplicationAttachmentRequest> addToimitaKopioSuorittamastasiKielitutkinnostaEnsimmaiseenAmmatillisenKoulutuksenHakutoiveeseenHakuajanLoppuunMennessaAttachments(
            final List<ApplicationAttachmentRequest> attachments,
            final Application application,
            final KoulutusinformaatioService koulutusinformaatioService,
            final String lang,
            final I18nBundle i18nBundle) {
        Map<String, String> answers = application.getPhaseAnswers(OppijaConstants.PHASE_GRADES);
        boolean eitherYleinenOrValtionhallinnonKielitutkinto = anyKeyTrue(resolveKielitutkintoKeys(), answers);
        if(eitherYleinenOrValtionhallinnonKielitutkinto) {
            Iterator<String> vocationalAoOids = ApplicationUtil.getVocationalAttachmentAOIds(application).iterator();
            if(vocationalAoOids.hasNext()) {
                final String firstVocationalAoOid = vocationalAoOids.next();
                ApplicationOptionDTO ao = koulutusinformaatioService.getApplicationOption(firstVocationalAoOid, lang);
                ApplicationAttachmentBuilder attachmentBuilder = ApplicationAttachmentBuilder.start()
                        .setName(i18nBundle.get("form.pyynto.toimittaa.kopio.todistuksesta.oppilaitokseen.nimi"))
                        .setDeliveryNote(i18nBundle.get(GENERAL_DELIVERY_NOTE))
                        .setAddress(getAddress(ao));
                Date deadline = ao.getAttachmentDeliveryDeadline();
                if (deadline == null) {
                    attachmentBuilder.setDeliveryNote(i18nBundle.get(GENERAL_DELIVERY_NOTE));
                }
                attachmentBuilder.setDescription(i18nBundle.get("form.pyynto.toimittaa.kopio.todistuksesta.oppilaitokseen.syy"));
                attachments.add(ApplicationAttachmentRequestBuilder.start()
                        .setPreferenceAoId(firstVocationalAoOid)
                        .setApplicationAttachment(attachmentBuilder.build())
                        .build());
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

    private static boolean liitepyynto(Application application, ApplicationOptionDTO ao, Pohjakoulutusliite pohjakoulutusliite) {
        if (!ApplicationUtil.hasBaseEducation(application, pohjakoulutusliite.formId)) {
            return false;
        }
        if (Pohjakoulutusliite.YO == pohjakoulutusliite) {
            return ApplicationUtil.hasBaseEducationYo(application) && ApplicationUtil.yoSuoritusvuosi(application) < 1990;
        }
        if (Pohjakoulutusliite.LUKIO == pohjakoulutusliite) {
            return ApplicationUtil.hasBaseEducationLukio(application);
        }
        if (ao.isJosYoEiMuitaLiitepyyntoja()) {
            return (Pohjakoulutusliite.KV_YO == pohjakoulutusliite ||
                    Pohjakoulutusliite.KV_YO_ULK == pohjakoulutusliite ||
                    !ApplicationUtil.hasBaseEducationYoOrKvYo(application));
        }
        return true;
    }

    public static Map<String, List<ApplicationOptionDTO>> pohjakoulutusliitepyynnot(Application application, List<ApplicationOptionDTO> aos) {
        Map<String, List<ApplicationOptionDTO>> liitepyynnot = new HashMap<>();
        for (ApplicationOptionDTO ao : aos) {
            if (ao.getPohjakoulutusLiitteet() != null) {
                for (String pohjakoulutuskoodi : ao.getPohjakoulutusLiitteet()) {
                    Pohjakoulutusliite p = Pohjakoulutusliite.byKoodiUri(pohjakoulutuskoodi);
                    if (liitepyynto(application, ao, p)) {
                        if (!liitepyynnot.containsKey(p.i18nKey)) {
                            liitepyynnot.put(p.i18nKey, new ArrayList<ApplicationOptionDTO>());
                        }
                        liitepyynnot.get(p.i18nKey).add(ao);
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
        Map<String, List<ApplicationOptionDTO>> higherEdAttachmentAOs = pohjakoulutusliitepyynnot(application, koulutusinformaatioService.getApplicationOptions(ApplicationUtil.getPreferenceAoIds(application), lang));
        Map<String, List<HigherEdBaseEducationAttachmentInfo>> higherEdAttachments = getAddresses(applicationSystem.getAttachmentGroupAddresses(), higherEdAttachmentAOs, deadline);
        attachments.addAll(getHigherEdAttachments(higherEdAttachments, i18nBundle));
        return attachments;
    }

    private static List<ApplicationAttachmentRequest> addAmkOpeAttachments(
            final ApplicationSystem applicationSystem,
            final List<ApplicationAttachmentRequest> attachments, final Application application,
            final KoulutusinformaatioService koulutusinformaatioService, final String lang,
            final I18nBundle i18nBundle) {

        Date deadline = null;
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

                if (null != address.helpText) {
                    attachmentBuilder.setDeliveryNote(address.helpText);
                } else if (null == address.deadline) {
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
            for (AttachmentGroupAddress groupAddress : attachmentGroupAddresses) {
                if (organizationGroup.getOid().equals(groupAddress.getGroupId())) {
                    return new HigherEdBaseEducationAttachmentInfo(
                            chooseAddress(groupAddress, aoAddress),
                            OriginatorType.group,
                            organizationGroup.getOid(),
                            null,
                            groupAddress.isUseFirstAoAddress() ? createI18NAsIs(ao.getProvider().getName()) : null,
                            groupAddress.getDeliveryDue() == null ? defaultDeadline : groupAddress.getDeliveryDue(),
                            groupAddress.getHelpText()
                    );
                }
            }
        }
        return aoAddress;
    }

    private static Address chooseAddress(AttachmentGroupAddress groupAddress, HigherEdBaseEducationAttachmentInfo aoAddress) {
        if (groupAddress.isUseFirstAoAddress()) {
            return aoAddress.address;
        }
        return getAddress(groupAddress.getDeliveryAddress());
    }

    private static HigherEdBaseEducationAttachmentInfo getAttachmentAddressInfo(ApplicationOptionDTO ao, Date deadline) {
        LearningOpportunityProviderDTO provider = ao.getProvider();
        return new HigherEdBaseEducationAttachmentInfo(
                selectPreferredAddress(ao),
                OriginatorType.applicationOption,
                ao.getId(),
                provider.getId(),
                createI18NAsIs(provider.getName()),
                deadline,
                null
        );
    }

    private static Address selectPreferredAddress(ApplicationOptionDTO ao) {
        LearningOpportunityProviderDTO provider = ao.getProvider();
        String recipientName = provider.getName();
        AddressDTO address = provider.getPostalAddress();
        if (ao.getApplicationOffice() != null) {
            recipientName = ao.getApplicationOffice().getName();
            address = ao.getApplicationOffice().getPostalAddress();
        } else if (provider.getApplicationOffice() != null && provider.getApplicationOffice().getPostalAddress() != null) {
            if (StringUtils.isNotEmpty(provider.getApplicationOffice().getName())) {
                recipientName = provider.getApplicationOffice().getName();
            }
            address = provider.getApplicationOffice().getPostalAddress();
        }
        return getAddress(recipientName, address);
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
