package fi.vm.sade.oppija.common.authentication.impl;

import fi.vm.sade.oppija.common.authentication.AuthenticationService;
import fi.vm.sade.oppija.common.authentication.Person;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * @author Hannu Lyytikainen
 */
@Service
//@Profile("dev")
public class AuthenticationServiceMockImpl implements AuthenticationService {

    private final String oidPrefix = "9.8.7.6.5.";

    public String addPerson(Person person) {
        return oidPrefix + String.valueOf(Math.round(Math.random() * 1000000000));
    }


}
