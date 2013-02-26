package fi.vm.sade.oppija.hakemus.service.impl;

import fi.vm.sade.service.hakemus.HakemusService;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import java.util.ArrayList;

/**
 * @author Mikko Majapuro
 */
@Service
public class HakemusServiceImpl implements HakemusService {

    public java.util.List<fi.vm.sade.service.hakemus.schema.HakemusTyyppi> haeHakemukset(
            @WebParam(name = "hakukohdeOid", targetNamespace = "")
            java.util.List<java.lang.String> hakukohdeOid) {
        return new ArrayList<fi.vm.sade.service.hakemus.schema.HakemusTyyppi>();
    }
}
