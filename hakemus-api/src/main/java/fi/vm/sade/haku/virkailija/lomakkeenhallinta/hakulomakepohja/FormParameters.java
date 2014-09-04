package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja;

import com.google.common.base.Joiner;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.ThemeQuestionConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import javax.ws.rs.HEAD;

public class FormParameters {
    private static final String FORM_MESSAGES = "form_messages";

    public enum FormTemplateType {
        YHTEISHAKU_KEVAT,
        YHTEISHAKU_SYKSY,
        YHTEISHAKU_SYKSY_KORKEAKOULU,
        LISAHAKU_SYKSY,
        LISAHAKU_KEVAT,
        PERVAKO;
    }

    private final ApplicationSystem applicationSystem;
    private final KoodistoService koodistoService;
    private final ThemeQuestionDAO themeQuestionDAO;
    private final HakukohdeService hakukohdeService;
    private final OrganizationService organizationService;

    private final FormTemplateType formTemplateType;
    private final I18nBundle i18nBundle;

    private Boolean onlyThemeGenerationForFormEditor = Boolean.FALSE;

    public FormParameters(final ApplicationSystem applicationSystem, final KoodistoService koodistoService,
                          final ThemeQuestionDAO themeQuestionDAO, final HakukohdeService hakukohdeService,
                          final OrganizationService organizationService) {
        this.applicationSystem = applicationSystem;
        this.koodistoService = koodistoService;
        this.themeQuestionDAO = themeQuestionDAO;
        this.hakukohdeService = hakukohdeService;
        this.organizationService = organizationService;
        this.formTemplateType = figureOutFormForApplicationSystem(applicationSystem);

        if (FormTemplateType.PERVAKO.equals(formTemplateType)) {
            this.i18nBundle = new I18nBundle(getMessageBundleName(FORM_MESSAGES, applicationSystem) + "_pervako");
        } else if (FormTemplateType.YHTEISHAKU_SYKSY_KORKEAKOULU.equals(formTemplateType)) {
            this.i18nBundle = new I18nBundle(getMessageBundleName(FORM_MESSAGES, applicationSystem) + "_korkeakoulu");
        } else {
            this.i18nBundle = new I18nBundle(getMessageBundleName(FORM_MESSAGES, applicationSystem));
        }
    }

    public ApplicationSystem getApplicationSystem() {
        return applicationSystem;
    }

    public KoodistoService getKoodistoService() {
        return koodistoService;
    }

    public FormTemplateType getFormTemplateType() {
        return formTemplateType;
    }

    private static String getMessageBundleName(final String baseName, final ApplicationSystem as) {
        String hakutyyppi = OppijaConstants.LISA_HAKU.equals(as.getApplicationSystemType()) ? "lisahaku" : "yhteishaku";
        String hakukausi = OppijaConstants.HAKUKAUSI_SYKSY.equals(as.getHakukausiUri()) ? "syksy" : "kevat";
        return Joiner.on('_').join(baseName, hakutyyppi, hakukausi);
    }

    private FormTemplateType figureOutFormForApplicationSystem(ApplicationSystem as) {
        if (OppijaConstants.KOHDEJOUKKO_PERVAKO.equals(as.getKohdejoukkoUri())) {
            return FormTemplateType.PERVAKO;
        } else if (OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(as.getKohdejoukkoUri())) {
            return FormTemplateType.YHTEISHAKU_SYKSY_KORKEAKOULU;
        }
        if (as.getApplicationSystemType().equals(OppijaConstants.LISA_HAKU)) {
            if (as.getHakukausiUri().equals(OppijaConstants.HAKUKAUSI_KEVAT)) {
                return FormTemplateType.LISAHAKU_KEVAT;
            }
            return FormTemplateType.LISAHAKU_SYKSY;
        } else {
            if (as.getHakukausiUri().equals(OppijaConstants.HAKUKAUSI_SYKSY)) {
                return FormTemplateType.YHTEISHAKU_SYKSY;
            } else if (as.getHakukausiUri().equals(OppijaConstants.HAKUKAUSI_KEVAT)) {
                return FormTemplateType.YHTEISHAKU_KEVAT;
            } else {
                return FormTemplateType.PERVAKO;
            }
        }
    }

    public I18nText getI18nText(final String key) {
        return this.i18nBundle.get(key);
    }

    public boolean isPervako() {
        return FormParameters.FormTemplateType.PERVAKO.equals(formTemplateType);
    }

    public boolean isHigherEd() {
        return FormTemplateType.YHTEISHAKU_SYKSY_KORKEAKOULU.equals(this.getFormTemplateType());
    }

    public boolean isKevaanLisahaku() {
        return FormTemplateType.LISAHAKU_KEVAT.equals(this.getFormTemplateType());
    }

    public boolean isKevaanYhteishaku() {
        return FormTemplateType.YHTEISHAKU_KEVAT.equals(this.getFormTemplateType());
    }
    public boolean isLisahaku() {
        return applicationSystem.getApplicationSystemType().equals(OppijaConstants.LISA_HAKU);
    }

    public ThemeQuestionConfigurator getThemeQuestionConfigurator() {
        return new ThemeQuestionConfigurator(themeQuestionDAO, hakukohdeService, this, organizationService);
    }

    public Boolean isOnlyThemeGenerationForFormEditor() {
        return onlyThemeGenerationForFormEditor;
    }

    public void setOnlyThemeGenerationForFormEditor(Boolean onlyThemeGenerationForFormEditor) {
        this.onlyThemeGenerationForFormEditor = onlyThemeGenerationForFormEditor;
    }
}
