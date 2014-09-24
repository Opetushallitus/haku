package fi.vm.sade.haku.oppija.lomake.exception;

public class IncoherentDataException extends RuntimeException {

    public IncoherentDataException(final String message) {
        super(message);
    }

    public IncoherentDataException(final String message, final Exception exception) {
        super(message, exception);
    }
}
