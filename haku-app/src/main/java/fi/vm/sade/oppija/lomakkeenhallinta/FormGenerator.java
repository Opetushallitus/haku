package fi.vm.sade.oppija.lomakkeenhallinta;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomakkeenhallinta.service.tarjonta.TarjontaService;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.Yhteishaku2013;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Map<String, Map<String, String>> applicationSystems = tarjontaService.getApplicationSystemOidsAndNames();
        for (String applicationSystemOid : applicationSystems.keySet()) {
            Yhteishaku2013 e = new Yhteishaku2013(koodistoService, applicationSystemOid, aoid);
            ApplicationPeriod ap = e.getApplicationPeriod();
            ap.setName(new I18nText(applicationSystems.get(applicationSystemOid)));
            forms.add(ap);
        }
        return ImmutableList.copyOf(forms);
    }


}
