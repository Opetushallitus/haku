package fi.vm.sade.haku;

import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationProviderForTesting implements AuthenticationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationProviderForTesting.class);
    private static Map<Object,Authentication> testAccountsByName = new HashMap<>();
    public static final String NOBODY_USER_OID = Types.PersonOid.of("1.2.246.562.24.00000000000").toString();
    public static final String OFFICER_USER_OID = Types.PersonOid.of("1.2.246.562.24.00000000001").toString();
    public static final String OTHER_OFFICER_USER_OID = Types.PersonOid.of("1.2.246.562.24.00000000002").toString();
    public static final String OPO_USER_OID = Types.PersonOid.of("1.2.246.562.24.00000000003").toString();
    public static final String OTHER_USER_OID = Types.PersonOid.of("1.2.246.562.24.00000000004").toString();

    public static final String NOBODY_LOGIN = "nobody";
    public static final String OFFICER_LOGIN = "officer";

    static {
        testAccountsByName.put(NOBODY_LOGIN, new TestingAuthenticationToken(NOBODY_USER_OID, null, "ROLE_NOBODY"));
        testAccountsByName.put("admin", new TestingAuthenticationToken(OTHER_USER_OID, null, "ROLE_APP_HAKEMUS_CRUD", "ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE", "ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD", "ROLE_APP_HAKULOMAKKEENHALLINTA_READ"));
        testAccountsByName.put("master", new TestingAuthenticationToken(OTHER_USER_OID, null, "ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD"));
        testAccountsByName.put(OFFICER_LOGIN, new TestingAuthenticationToken(OFFICER_USER_OID, null, "ROLE_APP_HAKEMUS_CRUD"));
        testAccountsByName.put("kkvirkailija", new TestingAuthenticationToken(OTHER_OFFICER_USER_OID, null, "ROLE_APP_HAKEMUS_CRUD", "ROLE_APP_HAKEMUS_KKVIRKAILIJA"));
        testAccountsByName.put("eikkvirkailija", new TestingAuthenticationToken(OTHER_OFFICER_USER_OID, null, "ROLE_APP_HAKEMUS_CRUD"));
        testAccountsByName.put("opo", new TestingAuthenticationToken(OPO_USER_OID, null, "ROLE_APP_HAKEMUS_OPO"));
        testAccountsByName.put("lisatieto", new TestingAuthenticationToken(OTHER_USER_OID, null, "ROLE_APP_HAKEMUS_LISATIETOCRUD"));
        testAccountsByName.put("heikkve", new TestingAuthenticationToken(OTHER_USER_OID, null, "ROLE_APP_HAKEMUS_CRUD"));
    }

    @Override
    public Authentication authenticate(Authentication input) throws AuthenticationException {
        String username = (String) input.getPrincipal();
        Authentication authentication = testAccountsByName.get(username);
        if (authentication != null) {
            return authentication;
        } else {
            LOGGER.warn("Could not find authentication for " + username);
        }
        return input;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
