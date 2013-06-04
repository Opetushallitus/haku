package fi.vm.sade.oppija.lomakkeenhallinta;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomakkeenhallinta.service.tarjonta.TarjontaService;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.Yhteishaku2013;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FormGenerator {
    private final TarjontaService tarjontaService;
    private final KoodistoService koodistoService;
    private final String aoid;

    @Autowired
    public FormGenerator(final TarjontaService tarjontaService,
                         final KoodistoService koodistoService,
                         @Value("${aoid}") String aoid) {
        this.tarjontaService = tarjontaService;
        this.koodistoService = koodistoService;
        this.aoid = aoid;
    }

    public List<ApplicationPeriod> generate() {
        List<ApplicationPeriod> forms = new ArrayList<ApplicationPeriod>();
        for (String applicationSystemOid : tarjontaService.getApplicationSystemOids()) {
            Yhteishaku2013 e = new Yhteishaku2013(koodistoService, applicationSystemOid, aoid);
            forms.add(e.getApplicationPeriod());
        }
        return ImmutableList.copyOf(forms);
    }


}
