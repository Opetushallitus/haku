package fi.vm.sade.oppija.hakemus.service.impl;

import fi.vm.sade.oppija.hakemus.dao.ApplicationQueryParameters;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.service.hakemus.HakemusService;
import fi.vm.sade.service.hakemus.schema.HakemusTyyppi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Service
public class HakemusServiceImpl implements HakemusService {

    @Autowired
    private ApplicationService applicationService;

    @Override
    public java.util.List<fi.vm.sade.service.hakemus.schema.HakemusTyyppi> haeHakemukset(
            @WebParam(name = "hakukohdeOid", targetNamespace = "")
            java.util.List<java.lang.String> hakukohdeOid) {
        try {
            applicationService.getApplication("123");
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //List<Application> applications = applicationService.findApplications("", new ApplicationQueryParameters(hakukohdeOid));
        List<fi.vm.sade.service.hakemus.schema.HakemusTyyppi> hakemukset = new ArrayList<HakemusTyyppi>();
        /*for (Application app : applications) {
            HakemusTyyppi ht = new HakemusTyyppi();
            ht.setHakemusOid(app.getOid());
            hakemukset.add(ht);
        } */
        return hakemukset;
    }
}
