package fi.vm.sade.haku.virkailija.lomakkeenhallinta.service;

import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
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
import org.springframework.stereotype.Service;


@Service
public final class FormConfigurationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormConfigurationService.class);

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
    public FormConfigurationService(final KoodistoService koodistoService,
                                    final HakuService hakuService,
                                    final ThemeQuestionDAO themeQuestionDAO,
                                    final HakukohdeService hakukohdeService,
                                    final OrganizationService organizationService,
                                    final FormConfigurationDAO formConfigurationDAO) {
        this.koodistoService = koodistoService;
        this.hakuService = hakuService;
        this.themeQuestionDAO = themeQuestionDAO;
        this.hakukohdeService = hakukohdeService;
        this.organizationService = organizationService;
        this.formConfigurationDAO = formConfigurationDAO;
    }

    public FormParameters getFormConfiguration(String applicationSystemId) {
        ApplicationSystem applicationSystem = hakuService.getApplicationSystem(applicationSystemId);
        return getFormConfiguration(applicationSystem);
    }

    public FormParameters getFormConfiguration(ApplicationSystem applicationSystem) {
        FormConfiguration formConfiguration = null;
        try {
            formConfiguration = formConfigurationDAO.findByApplicationSystem(applicationSystem.getId());
        } catch (Exception e) {
            LOGGER.warn("No configuration for application system", applicationSystem);
        }
        if (null == formConfiguration) {
            formConfiguration = new FormConfiguration(applicationSystem.getId(),
              figureOutFormTypeForApplicationSystem(applicationSystem));
        }
        return new FormParameters(applicationSystem, formConfiguration, koodistoService, themeQuestionDAO,
          hakukohdeService,
          organizationService);
    }

    public FormConfiguration.FormTemplateType defaultFormTemplateType(final String applicationSystemId) {
        ApplicationSystem applicationSystem = hakuService.getApplicationSystem(applicationSystemId);
        return figureOutFormTypeForApplicationSystem(applicationSystem);
    }

    public static FormConfiguration.FormTemplateType figureOutFormTypeForApplicationSystem(final ApplicationSystem as) {
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

}
