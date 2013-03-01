package fi.vm.sade.oppija.lomake.domain.exception;

public class ConfigurationException extends RuntimeException {

    private static final long serialVersionUID = -7119415673005159216L;

    public ConfigurationException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public ConfigurationException(final Throwable cause) {
        super(cause);
    }

    public ConfigurationException() {
        super();
    }
    
}
