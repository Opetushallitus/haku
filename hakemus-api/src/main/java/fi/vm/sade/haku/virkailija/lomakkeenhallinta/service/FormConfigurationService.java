package fi.vm.sade.haku.virkailija.lomakkeenhallinta.service;

import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.GroupConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.FormConfigurationDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public final class FormConfigurationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormConfigurationService.class);

    @Value("${formconfigurationservice.storeOnGenerate:true}")
    private boolean persistCreatedNewConfiguration;

    @Autowired
    private final ThemeQuestionDAO themeQuestionDAO;
    @Autowired
    private final HakukohdeService hakukohdeService;
    @Autowired
    private final OrganizationService organizationService;
    @Autowired
    private final KoodistoService koodistoService;
    @Autowired
    private final HakuService hakuService;
    @Autowired
    private final FormConfigurationDAO formConfigurationDAO;
    @Autowired
    private final I18nBundleService i18nBundleService;
    @Autowired
    private final HakumaksuService hakumaksuService;

    @Value("${mode.demo:false}")
    public boolean demoMode;
    @Value("${opintopolku.baseurl:https://opintopolku.fi}")
    public String opintopolkuBaseUrl;

    @Autowired
    public FormConfigurationService(final KoodistoService koodistoService,
                                    final HakuService hakuService,
                                    final ThemeQuestionDAO themeQuestionDAO,
                                    final HakukohdeService hakukohdeService,
                                    final OrganizationService organizationService,
                                    final FormConfigurationDAO formConfigurationDAO,
                                    final I18nBundleService i18nBundleService,
                                    final HakumaksuService hakumaksuService) {
        this.koodistoService = koodistoService;
        this.hakuService = hakuService;
        this.themeQuestionDAO = themeQuestionDAO;
        this.hakukohdeService = hakukohdeService;
        this.organizationService = organizationService;
        this.formConfigurationDAO = formConfigurationDAO;
        this.i18nBundleService = i18nBundleService;
        this.hakumaksuService = hakumaksuService;
    }

    public FormParameters getFormParameters(final String applicationSystemId) {
        ApplicationSystem applicationSystem = hakuService.getApplicationSystem(applicationSystemId);
        return getFormParameters(applicationSystem);
    }

    public FormParameters getFormParameters(final ApplicationSystem applicationSystem) {
        FormConfiguration formConfiguration = createOrGetFormConfiguration(applicationSystem);
        return new FormParameters(applicationSystem, formConfiguration, koodistoService, themeQuestionDAO,
                hakukohdeService, organizationService, i18nBundleService, hakumaksuService,
                this.demoMode, this.opintopolkuBaseUrl);
    }

    public FormConfiguration createOrGetFormConfiguration(final String applicationSystemId){
        FormConfiguration formConfiguration = formConfigurationDAO.findByApplicationSystem(applicationSystemId);
        if (null != formConfiguration)
            return formConfiguration;

        return createAndStoreFormConfiguration(applicationSystemId);
    }

    public FormConfiguration createOrGetFormConfiguration(final ApplicationSystem applicationSystem){
        FormConfiguration formConfiguration = formConfigurationDAO.findByApplicationSystem(applicationSystem.getId());
        if (null != formConfiguration)
            return formConfiguration;

        return createAndStoreFormConfiguration(applicationSystem);
    }

    private FormConfiguration createAndStoreFormConfiguration(final String applicationSystemId) {
        ApplicationSystem applicationSystem = hakuService.getApplicationSystem(applicationSystemId);
        if (null == applicationSystem)
            throw new ResourceNotFoundException("ApplicationSystem \"" + applicationSystemId + "\" cannot be resolved");
        return createAndStoreFormConfiguration(applicationSystem);
    }

    private FormConfiguration createAndStoreFormConfiguration(final ApplicationSystem applicationSystem){
        Map<FormConfiguration.FeatureFlag, Boolean> defaultFeatureFlags = new HashMap<>();
        defaultFeatureFlags.put(FormConfiguration.FeatureFlag.erotteleAmmatillinenJaYoAmmatillinenKeskiarvo, true);
        defaultFeatureFlags.put(FormConfiguration.FeatureFlag.koulutusasteURI, true);

        FormConfiguration formConfiguration = new FormConfiguration(applicationSystem.getId(),
                  figureOutFormTypeForApplicationSystem(applicationSystem), defaultFeatureFlags);
        if (persistCreatedNewConfiguration)
            return saveAndFetchFormConfiguration(formConfiguration);
        else
            return formConfiguration;
    }

    private FormConfiguration saveAndFetchFormConfiguration (final FormConfiguration formConfiguration){
        formConfigurationDAO.save(formConfiguration);
        return formConfigurationDAO.findByApplicationSystem(formConfiguration.getApplicationSystemId());
    }

    public FormConfiguration.FormTemplateType defaultFormTemplateType(final String applicationSystemId) {
        ApplicationSystem applicationSystem = hakuService.getApplicationSystem(applicationSystemId);
        return figureOutFormTypeForApplicationSystem(applicationSystem);
    }

    private static FormConfiguration.FormTemplateType figureOutFormTypeForApplicationSystem(final ApplicationSystem as) {
        if (OppijaConstants.KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA.equals(as.getKohdejoukkoUri())) {
            return FormConfiguration.FormTemplateType.PERUSOPETUKSEN_JALKEINEN_VALMENTAVA;
        } else if (OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(as.getKohdejoukkoUri())) {
            return FormConfiguration.FormTemplateType.YHTEISHAKU_SYKSY_KORKEAKOULU;
        }
        if (as.getApplicationSystemType().equals(OppijaConstants.HAKUTYYPPI_LISAHAKU)) {
            if (as.getHakukausiUri().equals(OppijaConstants.HAKUKAUSI_KEVAT)) {
                return FormConfiguration.FormTemplateType.LISAHAKU_KEVAT;
            }
            return FormConfiguration.FormTemplateType.LISAHAKU_SYKSY;
        } else {
            if (as.getHakukausiUri().equals(OppijaConstants.HAKUKAUSI_SYKSY)) {
                return FormConfiguration.FormTemplateType.YHTEISHAKU_SYKSY;
            } else if (as.getHakukausiUri().equals(OppijaConstants.HAKUKAUSI_KEVAT)) {
                return FormConfiguration.FormTemplateType.YHTEISHAKU_KEVAT;
            } else {
                return FormConfiguration.FormTemplateType.PERUSOPETUKSEN_JALKEINEN_VALMENTAVA;
            }
        }
    }

    //TODO: =RS= Mutex
    public void saveFormTemplateType(String applicationSystemId, String formTemplateType) {
        FormConfiguration formConfiguration = createOrGetFormConfiguration(applicationSystemId);
        formConfiguration.setFormTemplateType(FormConfiguration.FormTemplateType.valueOf(formTemplateType));
        formConfigurationDAO.update(formConfiguration);
    }

    //TODO: =RS= Mutex
    public void saveGroupConfiguration(String applicationSystemId, GroupConfiguration groupConfiguration) {
        String groupId = groupConfiguration.getGroupId();
        if (null == groupId || groupId.isEmpty())
            return;

        FormConfiguration formConfiguration = createOrGetFormConfiguration(applicationSystemId);
        GroupConfiguration toBeRemoved = getMatchingGroupConfiguration(groupId, formConfiguration);
        if (null != toBeRemoved)
            formConfiguration.removeGroupConfiguration(toBeRemoved);
        formConfiguration.addGroupConfiguration(groupConfiguration);
        formConfigurationDAO.update(formConfiguration);
    }

    //TODO: =RS= Mutex
    public void removeGroupConfiguration(String applicationSystemId, GroupConfiguration groupConfiguration) {
        String groupId = groupConfiguration.getGroupId();
        if (null == groupId || groupId.isEmpty())
            return;
        FormConfiguration formConfiguration = createOrGetFormConfiguration(applicationSystemId);
        GroupConfiguration toBeRemoved = getMatchingGroupConfiguration(groupId, formConfiguration);
        formConfiguration.removeGroupConfiguration(toBeRemoved);
        formConfigurationDAO.update(formConfiguration);
    }

    //TODO: =RS= Data should be in set or map to save this trouble.
    private GroupConfiguration getMatchingGroupConfiguration(String groupId, FormConfiguration formConfiguration){
        GroupConfiguration matching = null;
        for (GroupConfiguration groupConfiguration : formConfiguration.getGroupConfigurations()){
            if (groupConfiguration.getGroupId().equals(groupId)) {
                matching = groupConfiguration;
                break;
            }
        }
        return matching;
    }
}
