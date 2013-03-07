package fi.vm.sade.oppija.hakemus.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.service.hakemus.HakemusService;
import fi.vm.sade.service.hakemus.schema.HakemusTyyppi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Service
public class HakemusServiceImpl implements HakemusService {
    private ApplicationService applicationService;
    private ConversionService conversionService;

    @Autowired
    public HakemusServiceImpl(ApplicationService applicationService, ConversionService conversionService) {
        this.applicationService = applicationService;
        this.conversionService = conversionService;
    }

    @Override
    public java.util.List<fi.vm.sade.service.hakemus.schema.HakemusTyyppi> haeHakemukset(
            @WebParam(name = "hakukohdeOid", targetNamespace = "")
            java.util.List<java.lang.String> hakukohdeOid) {

        List<Application> applications = applicationService.getApplicationsByApplicationOption(hakukohdeOid);
        return Lists.transform(applications, new Function<Application, HakemusTyyppi>() {
            @Override
            public HakemusTyyppi apply(Application application) {
                return conversionService.convert(application, HakemusTyyppi.class);
            }
        });
    }

    /*@Override
    public List<HakutoiveTyyppi> haeHakutoiveet(@WebParam(name = "hakuOid", targetNamespace = "") String hakuOid) {
        List<Application> applications = applicationService.getApplicationsByApplicationSystem(hakuOid);
        return Lists.transform(applications, new Function<Application, HakutoiveTyyppi>() {
            @Override
            public HakutoiveTyyppi apply(Application application) {
                return conversionService.convert(application, HakutoiveTyyppi.class);
            }
        });
    }*/
}
