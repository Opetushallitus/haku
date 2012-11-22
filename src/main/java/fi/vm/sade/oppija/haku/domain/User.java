package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * @author jukka
 * @version 10/12/123:21 PM}
 * @since 1.1
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = AnonymousUser.class)
        })
public class User implements Serializable {

    private static final long serialVersionUID = -989381286044396123L;

    private String userName;

    public User(@JsonProperty(value = "userName") String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    @JsonIgnore
    public boolean isKnown() {
        return true;
    }
}
