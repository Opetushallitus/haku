package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja;

import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.OhjausparametritService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.domain.Ohjausparametri;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.AttachmentGroupConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.GroupRestrictionConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.ThemeQuestionConfigurator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;

public class FormParameters {

    private final ApplicationSystem applicationSystem;
    private final KoodistoService koodistoService;
    private final ThemeQuestionDAO themeQuestionDAO;
    private final HakukohdeService hakukohdeService;
    private final OrganizationService organizationService;
    private final OhjausparametritService ohjausparametritService;

    private final FormConfiguration formConfiguration;
    private final I18nBundle i18nBundle;

    private Boolean onlyThemeGenerationForFormEditor = Boolean.FALSE;

    private boolean demoMode;

    public FormParameters(final ApplicationSystem applicationSystem,
                          final FormConfiguration formConfiguration,
                          final KoodistoService koodistoService,
                          final ThemeQuestionDAO themeQuestionDAO,
                          final HakukohdeService hakukohdeService,
                          final OrganizationService organizationService,
                          final I18nBundleService i18nBundleService,
                          final OhjausparametritService ohjausparametritService,
                          final boolean demoMode) {
        this.applicationSystem = applicationSystem;
        this.koodistoService = koodistoService;
        this.themeQuestionDAO = themeQuestionDAO;
        this.hakukohdeService = hakukohdeService;
        this.organizationService = organizationService;
        this.formConfiguration = formConfiguration;
        this.i18nBundle = i18nBundleService.getBundle(applicationSystem);
        this.ohjausparametritService = ohjausparametritService;
        this.demoMode = demoMode;
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

    public boolean isToisenAsteenHaku(){
        return OppijaConstants.TOISEN_ASTEEN_HAKUJEN_KOHDEJOUKOT.contains(applicationSystem.getKohdejoukkoUri());
    }

    public boolean isErityisopetuksenaJarjestettavaAmmatillinen(){
        return applicationSystem.getKohdejoukkoUri().equals(KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN);
    }

    public boolean isHuoltajanTiedotKysyttava() {
        return isHuoltajanTiedotKysyttava(applicationSystem);
    }

    public static boolean isHuoltajanTiedotKysyttava(ApplicationSystem applicationSystem) {
        return KOHDEJOUKKO_PERUSOPETUKSEN_JALKEINEN_VALMENTAVA.equals(applicationSystem.getKohdejoukkoUri())
                || KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO.equals(applicationSystem.getKohdejoukkoUri())
                || KOHDEJOUKKO_AMMATILLINEN_ERITYISOPETYKSENA.equals(applicationSystem.getKohdejoukkoUri())
                || KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN.equals(applicationSystem.getKohdejoukkoUri());
    }

    public static boolean isHigherEd(ApplicationSystem applicationSystem) {
        return applicationSystem.getKohdejoukkoUri().equals(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU);
    }

    public boolean isHigherEd() {
        return isHigherEd(applicationSystem);
    }

    public boolean isTarkistaPohjakoulutusRiittavyys() {
        if(isHigherEd() && StringUtils.isEmpty(applicationSystem.getKohdejoukonTarkenne())) {
            return true;
        }
        return false;
    }

    public boolean isErillishaku() {
        return OppijaConstants.HAKUTAPA_ERILLISHAKU.equals(applicationSystem.getHakutapa());
    }

    public boolean isYhteishaku() {
        return OppijaConstants.HAKUTAPA_YHTEISHAKU.equals(applicationSystem.getHakutapa());
    }

    public boolean isToisenAsteenErillishaku() {
        return OppijaConstants.HAKUTAPA_ERILLISHAKU.equals(applicationSystem.getHakutapa())
                && OppijaConstants.TOISEN_ASTEEN_HAKUJEN_KOHDEJOUKOT.contains(applicationSystem.getKohdejoukkoUri());
    }

    public boolean askOldEducationInfo() {
        if(isHigherEd() && isErillishaku()) {
            Ohjausparametri kysyVanhaaKoulutusta = ohjausparametritService.fetchOhjausparametritForHaku(applicationSystem.getId()).getPH_KVT();
            if(null != kysyVanhaaKoulutusta && null != kysyVanhaaKoulutusta.getBooleanValue()) {
                return kysyVanhaaKoulutusta.getBooleanValue();
            }
        }
        return true;
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
        return HAKUTYYPPI_VARSINAINEN_HAKU.equals(applicationSystem.getApplicationSystemType()) &&
                !(HAKUTAPA_ERILLISHAKU.equals(applicationSystem.getHakutapa()) &&
                        TOISEN_ASTEEN_HAKUJEN_KOHDEJOUKOT.contains(applicationSystem.getKohdejoukkoUri()));
    }

    public boolean isEmailRequired() {
        return formConfiguration.getFeatureFlag(FormConfiguration.FeatureFlag.requireEmail);
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
                && HAKUTYYPPI_VARSINAINEN_HAKU.equals(applicationSystem.getApplicationSystemType())){
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

    public int getTutkintoCountMax() {
        Date firstStarted = new Date(Long.MAX_VALUE);
        for (ApplicationPeriod period : applicationSystem.getApplicationPeriods()) {
            if (period.getStart().before(firstStarted)) {
                firstStarted = period.getStart();
            }
        }
        Calendar flagDay = GregorianCalendar.getInstance();
        flagDay.set(Calendar.YEAR, 2015);
        flagDay.set(Calendar.MONTH, Calendar.AUGUST);
        flagDay.set(Calendar.DAY_OF_MONTH, 1);

        if (flagDay.getTime().before(firstStarted)) {
            return 2;
        }
        return "1.2.246.562.29.95390561488".equals(applicationSystem.getId())
                ? 4
                : 5;
    }


    public boolean kysytaankoErityisopetuksenTarve() {
        return applicationSystem.getKohdejoukkoUri().equals(KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN);
    }

    public static boolean kysytaankoHarkinnanvaraisuus(ApplicationSystem applicationSystem) {
        return KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO.equals(applicationSystem.getKohdejoukkoUri());
    }

    public boolean kysytaankoHarkinnanvaraisuus() {
        return kysytaankoHarkinnanvaraisuus(applicationSystem);
    }

    public boolean kysytaankoSora() {
        return !isPerusopetuksenJalkeinenValmentava();
    }

    public boolean kysytaankoUrheilijanLisakysymykset() {
        return ! (isPerusopetuksenJalkeinenValmentava() || isHigherEd() ||
            applicationSystem.getKohdejoukkoUri().equals(KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN));
    }

    public boolean kysytaankoOppisopimuskysymys() {
        return KOHDEJOUKKO_AMMATILLINEN_JA_LUKIO.equals(applicationSystem.getKohdejoukkoUri()) &&
                OppijaConstants.HAKUTAPA_YHTEISHAKU.equals(applicationSystem.getHakutapa());
    }

    public boolean kysytaankoYlioppilastutkinto() {
        return !isPerusopetuksenJalkeinenValmentava();
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

    public boolean useGradeAverage() {
        return formConfiguration.getFeatureFlag(FormConfiguration.FeatureFlag.gradeAverageKomponentti);
    }

    public boolean useOptionalGradeAverageLukio() {
        return formConfiguration.getFeatureFlag(FormConfiguration.FeatureFlag.lukioKeskiarvoVapaaehtoinen);
    }

    public boolean useOptionalGradeAverageAmmatillinen() {
        return formConfiguration.getFeatureFlag(FormConfiguration.FeatureFlag.ammatillinenKeskiarvoVapaaehtoinen);
    }

    public boolean useEducationDegreeURI() {
        return formConfiguration.getFeatureFlag(FormConfiguration.FeatureFlag.koulutusasteURI);
    }

    public boolean gradeAverageLukio() {
        return formConfiguration.getFeatureFlag(FormConfiguration.FeatureFlag.gradeAverageLukio);
    }

    public boolean gradeAverageYoAmmatillinen() {
        return formConfiguration.getFeatureFlag(FormConfiguration.FeatureFlag.gradeAverageYoAmmatillinen);
    }

    public boolean gradeAverageAmmatillinen() {
        return formConfiguration.getFeatureFlag(FormConfiguration.FeatureFlag.gradeAverageAmmatillinen);
    }

    public boolean additionalInfoForPreviousDegree() {
        return formConfiguration.getFeatureFlag(FormConfiguration.FeatureFlag.additionalInfoForPreviousDegree);
    }

    public boolean isDemoMode() {
        return this.demoMode;
    }

    public boolean isKansainvalinenYoAmkKysymys() {
        return formConfiguration.getFeatureFlag(FormConfiguration.FeatureFlag.kansainvalinenYoAmkKysymys);
    }

    public boolean isSahkoinenViestintaLupa() {
        return formConfiguration.getFeatureFlag(FormConfiguration.FeatureFlag.sahkoinenViestintaLupa);
    }

    public boolean isAMKOpeMuutTutkinnotKysymys() {
        return formConfiguration.getFeatureFlag(FormConfiguration.FeatureFlag.amkOpeMuutTutkinnotKysymys);
    }

}
