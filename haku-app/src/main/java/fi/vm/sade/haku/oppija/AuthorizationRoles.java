package fi.vm.sade.haku.oppija;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.ROOT_ORGANIZATION_OID;

public class AuthorizationRoles {
    public final static String ALLOWED_FOR_ADMIN = "hasAnyRole('ROLE_APP_HAKEMUS_CRUD_" + ROOT_ORGANIZATION_OID + "')";
}
