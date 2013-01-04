package fi.vm.sade.oppija.hakemus.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Error message object that is provided to a client in case an API invocation results
 * in an error.
 *
 * @author Hannu Lyytikainen
 */
public class ErrorMessage {

    private String message;

    public ErrorMessage(@JsonProperty(value = "message") String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
