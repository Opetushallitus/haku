package fi.vm.sade.oppija.tarjonta.domain.exception;

public class SearchException extends RuntimeException {

    private static final long serialVersionUID = -3844853942795391944L;

    public SearchException(String message) {
        super(message);
    }

    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
