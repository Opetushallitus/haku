package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja;

import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.AttachmentGroupConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.GroupRestrictionConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.ThemeQuestionConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public class FormParameters {

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
                          final OrganizationService organizationService, final I18nBundleService i18nBundleService) {
        this.applicationSystem = applicationSystem;
        this.koodistoService = koodistoService;
        this.themeQuestionDAO = themeQuestionDAO;
        this.hakukohdeService = hakukohdeService;
        this.organizationService = organizationService;
        this.formConfiguration = formConfiguration;
        this.i18nBundle = i18nBundleService.getBundle(applicationSystem);
    }

    public ApplicationSystem getApplicationSystem() {
        return applicationSystem;
    }

    public KoodistoService getKoodistoService() {
        return koodistoService;
    }

    public FormConfiguration getFormConfiguration() {
        return formConfiguration;
    }

    private FormConfiguration.FormTemplateType getFormTemplateType() {
        return formConfiguration.getFormTemplateType();
    }

    public I18nText getI18nText(final String key) {
        return this.i18nBundle.get(key);
    }


    public boolean isAmmattillinenOpettajaKoulutus(){
        return getFormTemplateType().equals(FormConfiguration.FormTemplateType.AMK_OPET);
    }

    public boolean isAmmattillinenEritysopettajaTaiOppilaanohjaajaKoulutus(){
        return getFormTemplateType().equals(FormConfiguration.FormTemplateType.AMK_ERKAT_JA_OPOT);
    }

    public boolean isPerusopetuksenJalkeinenValmentava() {
        return FormConfiguration.FormTemplateType.PERUSOPETUKSEN_JALKEINEN_VALMENTAVA.equals(this.getFormTemplateType());
    }

    public boolean isKoulutustaustaMuuKoulutus() {
        return isPerusopetuksenJalkeinenValmentava() ||
                applicationSystem.getKohdejoukkoUri().equals(KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN);
    }

    public boolean isAmmatillinenTutkintoEstaaHakemisen() {
        return ! applicationSystem.getKohdejoukkoUri().equals(KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN);
    }

    public boolean isOpetuspisteetVetovalikossa() {
        return applicationSystem.getKohdejoukkoUri().equals(KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN);
    }

    public boolean isHuoltajanTiedotKysyttava() {
        if (KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA.equals(applicationSystem.getKohdejoukkoUri())) {
            return true;
        }
        if (KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO.equals(applicationSystem.getKohdejoukkoUri())
                && HAKUTAPA_YHTEISHAKU.equals(applicationSystem.getHakutapa())
                && (HAKUTYYPPI_VARSINAINEN_HAKU.equals(applicationSystem.getApplicationSystemType())
                    || HAKUTYYPPI_LISAHAKU.equals(applicationSystem.getApplicationSystemType()))) {
            return true;
        }
        if ((KOHDEJOUKKO_AMMATILLINEN_ERITYISOPETYKSENA.equals(applicationSystem.getKohdejoukkoUri())
             || KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN.equals(applicationSystem.getKohdejoukkoUri()))
            && HAKUTAPA_YHTEISHAKU.equals(applicationSystem.getHakutapa())
                && (HAKUTYYPPI_VARSINAINEN_HAKU.equals(applicationSystem.getApplicationSystemType())
                || HAKUTYYPPI_LISAHAKU.equals(applicationSystem.getApplicationSystemType()))) {
            return true;
        }
        return false;
    }

    public boolean isHigherEd() {
        return applicationSystem.getKohdejoukkoUri().equals(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU);
    }

    public boolean isKevaanLisahaku() {
        return FormConfiguration.FormTemplateType.LISAHAKU_KEVAT.equals(this.getFormTemplateType());
    }

    public boolean isKevaanYhteishaku() {
        return FormConfiguration.FormTemplateType.YHTEISHAKU_KEVAT.equals(this.getFormTemplateType());
    }
    public boolean isLisahaku() {
        return applicationSystem.getApplicationSystemType().equals(HAKUTYYPPI_LISAHAKU);
    }

    public boolean isUniqueApplicantRequired() {
        return HAKUTYYPPI_VARSINAINEN_HAKU.equals(applicationSystem.getApplicationSystemType());
    }

    public ThemeQuestionConfigurator getThemeQuestionConfigurator() {
        return new ThemeQuestionConfigurator(themeQuestionDAO, hakukohdeService, this, organizationService);
    }

    public GroupRestrictionConfigurator getGroupRestrictionConfigurator() {
        return new GroupRestrictionConfigurator(this, hakukohdeService, organizationService);
    }

    public AttachmentGroupConfigurator getAttachmentGroupConfigurator() {
        return new AttachmentGroupConfigurator(formConfiguration);
    }

    public Boolean isOnlyThemeGenerationForFormEditor() {
        return onlyThemeGenerationForFormEditor;
    }

    public void setOnlyThemeGenerationForFormEditor(Boolean onlyThemeGenerationForFormEditor) {
        this.onlyThemeGenerationForFormEditor = onlyThemeGenerationForFormEditor;
    }

    public List<String> getAllowedLanguages() {
        if (KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO.equals(applicationSystem.getKohdejoukkoUri())
                && HAKUTAPA_YHTEISHAKU.equals(applicationSystem.getHakutapa())
                && HAKUTYYPPI_VARSINAINEN_HAKU.equals(applicationSystem.getApplicationSystemType())
                && new Integer(2014).equals(applicationSystem.getHakukausiVuosi())
                && OppijaConstants.HAKUKAUSI_SYKSY.equals(applicationSystem.getHakukausiUri())){
            return asList("fi", "sv");
        }
        if (isAmmattillinenOpettajaKoulutus()) {
            return asList("fi", "en");
        }
        if (isAmmattillinenEritysopettajaTaiOppilaanohjaajaKoulutus()) {
            return asList("fi");
        }
        return asList("fi", "sv", "en");
    }

    private List<String> asList(String... langs) {
        List<String> list = new ArrayList<String>(langs.length);
        for (String lang : langs) {
            list.add(lang);
        }
        return list;
    }

    public boolean isAmmatillinenDropdown() {
        return applicationSystem.getKohdejoukkoUri().equals(KOHDEJOUKKO_KORKEAKOULU);
    }

    public boolean kysytaankoErityisopetuksenTarve() {
        return applicationSystem.getKohdejoukkoUri().equals(KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN);
    }

    public boolean kysytaankoHarkinnanvaraisuus() {
        return ! (isPerusopetuksenJalkeinenValmentava() || isHigherEd());
    }

    public boolean kysytaankoSora() {
        return !isPerusopetuksenJalkeinenValmentava();
    }

    public boolean kysytaankoUrheilijanLisakysymykset() {
        return ! (isPerusopetuksenJalkeinenValmentava() || isHigherEd() ||
                applicationSystem.getKohdejoukkoUri().equals(KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN));
    }

    public boolean kysytaankoYlioppilastutkinto() {
        return !isPerusopetuksenJalkeinenValmentava();
    }

    public boolean kysytaankoKielitaitokysymykset() {
        return ! (isPerusopetuksenJalkeinenValmentava() ||
                applicationSystem.getKohdejoukkoUri().equals(KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN));
    }

    public boolean kysytaankoUlkomaisenTutkinnonTarkennus() {
        return isPerusopetuksenJalkeinenValmentava();
    }

    public boolean kysytaankoTyokokemus() {
        return !isPerusopetuksenJalkeinenValmentava() && !isHigherEd();
    }

    public boolean kysytaankoKaksoistutkinto() {
        return !isPerusopetuksenJalkeinenValmentava() && !isHigherEd();
    }
}
