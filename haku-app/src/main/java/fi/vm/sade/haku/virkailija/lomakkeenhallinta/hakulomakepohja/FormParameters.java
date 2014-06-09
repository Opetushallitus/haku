package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja;

import com.google.common.base.Joiner;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

public class FormParameters {
    private static final String FORM_MESSAGES = "form_messages";


    public enum FormTemplateType {
        YHTEISHAKU_KEVAT,
        YHTEISHAKU_SYKSY,
        LISAHAKU_SYKSY,
        PERVAKO;
    }


    private final ApplicationSystem applicationSystem;
    private final KoodistoService koodistoService;

    private final FormTemplateType formTemplateType;
    private final I18nBundle i18nBundle;
    private final String formMessagesBundle;


    public FormParameters(final ApplicationSystem applicationSystem, final KoodistoService koodistoService) {
        this.applicationSystem = applicationSystem;
        this.koodistoService = koodistoService;
        this.formTemplateType = figureOutFormForApplicationSystem(applicationSystem);


        if (FormTemplateType.PERVAKO.equals(formTemplateType)) {
            this.formMessagesBundle = getMessageBundleName(FORM_MESSAGES, applicationSystem) + "_pervako";
        } else {
            this.formMessagesBundle = getMessageBundleName(FORM_MESSAGES, applicationSystem);
        }
        i18nBundle = new I18nBundle(this.formMessagesBundle);
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

    private FormTemplateType configureHuuhaa(ApplicationSystem as) {
        FormTemplateType ftt = null;
        // tff = ApplicationSystemConfigurations.fetch (as)
        if (null != ftt) {
            ftt = figureOutFormForApplicationSystem(as);
        }
        return ftt;
    }

    private FormTemplateType figureOutFormForApplicationSystem(ApplicationSystem as) {
        if (OppijaConstants.KOHDEJOUKKO_PERVAKO.equals(as.getKohdejoukkoUri())) {
            return FormTemplateType.PERVAKO;
        }
        if (as.getApplicationSystemType().equals(OppijaConstants.LISA_HAKU)) {
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
        I18nText i18nText = this.i18nBundle.get(key);
        return i18nText;
    }

    public I18nBundle getI18nBundle() {
        return i18nBundle;
    }

    public boolean isPervako() {
        return FormParameters.FormTemplateType.PERVAKO.equals(this.getFormTemplateType());
    }
}
