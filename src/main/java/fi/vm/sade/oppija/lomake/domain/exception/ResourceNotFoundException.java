package fi.vm.sade.oppija.lomake.domain.exception;

/**
 * Thrown when a requested resource is not found.
 *
 * @author Hannu Lyytikainen
 */
public class ResourceNotFoundException extends Exception {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
