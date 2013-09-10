package fi.vm.sade.oppija.lomake.exception;

public class ConfigurationException extends RuntimeException {

    private static final long serialVersionUID = -7119415673005159216L;

    public ConfigurationException(final Throwable cause) {
        super(cause);
    }

    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
