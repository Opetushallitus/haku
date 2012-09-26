package fi.vm.sade.oppija.haku.domain.exception;

/**
 * @author jukka
 * @version 9/10/1210:29 AM}
 * @since 1.1
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(final String message) {
        super(message);
    }
}
