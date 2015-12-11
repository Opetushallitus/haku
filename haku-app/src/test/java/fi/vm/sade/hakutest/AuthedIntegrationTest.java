package fi.vm.sade.hakutest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.PersonOid;

public abstract class AuthedIntegrationTest extends IntegrationTest {

    @BeforeClass
    public static void classSetup() {
        setAuthentication(PersonOid.of("1.24.1"), "ROLE_APP_HAKEMUS_CRUD");
    }

    @AfterClass
    public static void classTearDown() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public static void setAuthentication(PersonOid authenticatedPersonOid, String... roles) {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(authenticatedPersonOid.getValue(), "", roles));
    }
}
