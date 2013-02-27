package fi.vm.sade.oppija.hakemus.converter;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.service.hakemus.schema.AvainArvoTyyppi;
import fi.vm.sade.service.hakemus.schema.HakemusTyyppi;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

/**
 * @author Mikko Majapuro
 */

public class ApplicationToHakemusTyyppi implements Converter<Application, HakemusTyyppi> {

    @Override
    public HakemusTyyppi convert(Application application) {
        HakemusTyyppi hakemusTyyppi = new HakemusTyyppi();
        hakemusTyyppi.setHakemusOid(application.getOid());

        for (Map.Entry<String, String> entry : application.getVastauksetMerged().entrySet()) {
            AvainArvoTyyppi avainArvo = new AvainArvoTyyppi();
            avainArvo.setAvain(entry.getKey());
            avainArvo.setArvo(entry.getValue());
            hakemusTyyppi.getAvainArvo().add(avainArvo);
        }

        return hakemusTyyppi;
    }
}
