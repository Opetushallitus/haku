package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja;

import com.google.common.base.Joiner;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

public class FormParameters {
    private static final String FORM_MESSAGES = "form_messages";
    private static final String FORM_ERRORS = "form_errors";
    private static final String FORM_VERBOSE_HELP = "form_verboseHelp";

    public enum FormTemplateType {
        YHTEISHAKU_KEVAT,
        YHTEISHAKU_SYKSY,
        LISAHAKU_SYKSY,
        PERVAKO
    }

    private final ApplicationSystem applicationSystem;
    private final KoodistoService koodistoService;
    private final String formMessagesBundle;
    private final String formErrorsBundle;
    private final String formVerboseHelpBundle;
    private final FormTemplateType formTemplateType;

    public FormParameters(ApplicationSystem applicationSystem, KoodistoService koodistoService) {
        this.applicationSystem = applicationSystem;
        this.koodistoService = koodistoService;
        this.formMessagesBundle = getMessageBundleName(FORM_MESSAGES, applicationSystem);
        this.formErrorsBundle = getMessageBundleName(FORM_ERRORS, applicationSystem);
        this.formVerboseHelpBundle = getMessageBundleName(FORM_VERBOSE_HELP, applicationSystem);
        this.formTemplateType = figureOutFormForApplicationSystem(applicationSystem);
    }

    public ApplicationSystem getApplicationSystem() {
        return applicationSystem;
    }

    public KoodistoService getKoodistoService() {
        return koodistoService;
    }

    public String getFormMessagesBundle() {
        return formMessagesBundle;
    }

    public String getFormErrorsBundle() {
        return formErrorsBundle;
    }

    public String getFormVerboseHelpBundle() {
        return formVerboseHelpBundle;
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
}
