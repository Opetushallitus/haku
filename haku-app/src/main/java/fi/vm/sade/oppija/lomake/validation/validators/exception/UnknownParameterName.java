package fi.vm.sade.oppija.lomake.validation.validators.exception;

public class UnknownParameterName extends RuntimeException {
    private static final long serialVersionUID = -6424715291649429208L;

    public UnknownParameterName(String message) {
        super(message);
    }
}
