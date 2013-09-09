package fi.vm.sade.oppija.lomake.exception;

public class ElementNotFound extends ResourceNotFoundExceptionRuntime {
    public ElementNotFound(final String id) {
        super("Element " + id + " not found");
    }
}
