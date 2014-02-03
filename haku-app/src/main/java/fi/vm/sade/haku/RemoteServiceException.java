package fi.vm.sade.haku;

public class RemoteServiceException extends RuntimeException {
    public RemoteServiceException(final String message, Exception e) {
        super(message, e);
    }
}
