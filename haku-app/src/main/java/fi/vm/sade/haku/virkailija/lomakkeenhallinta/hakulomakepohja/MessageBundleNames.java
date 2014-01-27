package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja;

public class MessageBundleNames {
    private final String formMessages;
    private final String formErrors;
    private final String formVerboseHelp;

    public MessageBundleNames(final String formMessages, final String formErrors, final String formVerboseHelp) {
        this.formMessages = formMessages;
        this.formErrors = formErrors;
        this.formVerboseHelp = formVerboseHelp;
    }

    public String getFormMessages() {
        return formMessages;
    }

    public String getFormErrors() {
        return formErrors;
    }

    public String getFormVerboseHelp() {
        return formVerboseHelp;
    }
}
