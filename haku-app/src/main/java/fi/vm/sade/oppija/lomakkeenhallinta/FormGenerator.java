package fi.vm.sade.oppija.lomakkeenhallinta;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
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

    public List<ApplicationSystem> generate() {
        List<ApplicationSystem> forms = new ArrayList<ApplicationSystem>();
        Map<String, Map<String, String>> applicationSystems = tarjontaService.getApplicationSystemOidsAndNames();
        for (Map.Entry<String, Map<String, String>> applicationSystem : applicationSystems.entrySet()) {
            List<ApplicationPeriod> applicationPeriods = tarjontaService.getApplicationPeriods(applicationSystem.getKey());
            Yhteishaku2013 e = new Yhteishaku2013(koodistoService,
                    applicationSystem.getKey(), aoid, new I18nText(applicationSystems.get(applicationSystem.getKey())),
                    applicationPeriods);
            forms.add(e.getApplicationSystem());
        }
        return ImmutableList.copyOf(forms);
    }


}
