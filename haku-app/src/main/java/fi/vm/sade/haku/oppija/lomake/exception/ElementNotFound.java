package fi.vm.sade.haku.oppija.lomake.exception;

public class ElementNotFound extends ResourceNotFoundExceptionRuntime {
    public ElementNotFound(final String id) {
        super("Element " + id + " not found");
    }
}
