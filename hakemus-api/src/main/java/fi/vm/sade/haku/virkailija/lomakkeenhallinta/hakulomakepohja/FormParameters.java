package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja;

import com.google.common.base.Joiner;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.ThemeQuestionConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.HashMap;
import java.util.Map;

public class FormParameters {
    private static final String FORM_MESSAGES = "form_messages";

    private static final Map<String, String> HAKUTAVAT = new HashMap<String, String>() {{
        put(OppijaConstants.HAKUTAPA_YHTEISHAKU, "yhteishaku");
        put(OppijaConstants.HAKUTAPA_ERILLISHAKU, "erillishaku");
        put(OppijaConstants.HAKUTAPA_JATKUVA_HAKU, "jatkuva");
    }};

    private static final Map<String, String> HAKUTYYPIT = new HashMap<String, String>() {{
        put(OppijaConstants.HAKUTYYPPI_VARSINAINEN_HAKU, "varsinainen");
        put(OppijaConstants.HAKUTYYPPI_TAYDENNYS, "taydennys");
        put(OppijaConstants.HAKUTYYPPI_LISAHAKU, "lisahaku");
    }};

    private static final Map<String, String> KOHDEJOUKOT = new HashMap<String, String>() {{
        put(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU, "korkeakoulu");
        put(OppijaConstants.KOHDEJOUKKO_PERVAKO, "pervako");
    }};

    private static final Map<String, String> HAKUKAUDET = new HashMap<String, String>() {{
        put(OppijaConstants.HAKUKAUSI_KEVAT, "kevat");
        put(OppijaConstants.HAKUKAUSI_SYKSY, "syksy");
    }};

    private final ApplicationSystem applicationSystem;
    private final KoodistoService koodistoService;
    private final ThemeQuestionDAO themeQuestionDAO;
    private final HakukohdeService hakukohdeService;
    private final OrganizationService organizationService;

    private final FormConfiguration formConfiguration;
    private final I18nBundle i18nBundle;

    private Boolean onlyThemeGenerationForFormEditor = Boolean.FALSE;

    public FormParameters(final ApplicationSystem applicationSystem, final FormConfiguration formConfiguration, final KoodistoService koodistoService,
                          final ThemeQuestionDAO themeQuestionDAO, final HakukohdeService hakukohdeService,
                          final OrganizationService organizationService) {
        this.applicationSystem = applicationSystem;
        this.koodistoService = koodistoService;
        this.themeQuestionDAO = themeQuestionDAO;
        this.hakukohdeService = hakukohdeService;
        this.organizationService = organizationService;
        this.formConfiguration = formConfiguration;
        this.i18nBundle = new I18nBundle(getMessageBundleName(FORM_MESSAGES, applicationSystem));
    }

    public ApplicationSystem getApplicationSystem() {
        return applicationSystem;
    }

    public KoodistoService getKoodistoService() {
        return koodistoService;
    }

    private FormConfiguration.FormTemplateType getFormTemplateType() {
        return formConfiguration.getFormTemplateType();
    }

    private static String getMessageBundleName(final String baseName, final ApplicationSystem as) {
        return Joiner.on('_').join(baseName,
                HAKUTAVAT.get(as.getHakutapa()),
                HAKUTYYPIT.get(as.getApplicationSystemType()),
                HAKUKAUDET.get(as.getHakukausiUri()),
                KOHDEJOUKOT.containsKey(as.getKohdejoukkoUri()) ? KOHDEJOUKOT.get(as.getKohdejoukkoUri()) : "muu");
    }

    public I18nText getI18nText(final String key) {
        return this.i18nBundle.get(key);
    }

    public boolean isPervako() {
        return FormConfiguration.FormTemplateType.PERVAKO.equals(this.getFormTemplateType());
    }

    public boolean isHigherEd() {
        return FormConfiguration.FormTemplateType.YHTEISHAKU_SYKSY_KORKEAKOULU.equals(this.getFormTemplateType());
    }

    public boolean isKevaanLisahaku() {
        return FormConfiguration.FormTemplateType.LISAHAKU_KEVAT.equals(this.getFormTemplateType());
    }

    public boolean isKevaanYhteishaku() {
        return FormConfiguration.FormTemplateType.YHTEISHAKU_KEVAT.equals(this.getFormTemplateType());
    }
    public boolean isLisahaku() {
        return applicationSystem.getApplicationSystemType().equals(OppijaConstants.HAKUTYYPPI_LISAHAKU);
    }

    public boolean isUniqueApplicantRequired() {
        return OppijaConstants.HAKUTYYPPI_VARSINAINEN_HAKU.equals(applicationSystem.getApplicationSystemType());
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
