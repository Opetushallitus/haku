package fi.vm.sade.oppija.common.authentication.impl;

import fi.vm.sade.oppija.common.authentication.AuthenticationService;
import fi.vm.sade.oppija.common.authentication.Person;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile("dev")
public class AuthenticationServiceMockImpl implements AuthenticationService {

    private final String oidPrefix = "1.2.246.562.24.";

    public String addPerson(Person person) {
        return oidPrefix + String.format("%011d", Math.round(Math.random() * 1000000000));
    }


}
