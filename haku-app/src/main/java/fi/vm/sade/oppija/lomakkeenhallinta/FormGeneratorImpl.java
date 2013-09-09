package fi.vm.sade.oppija.lomakkeenhallinta;

import com.google.common.collect.Lists;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.common.tarjonta.HakuService;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.Yhteishaku2013;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormGeneratorImpl implements FormGenerator {
    private final KoodistoService koodistoService;
    private final HakuService hakuService;

    @Autowired
    public FormGeneratorImpl(final KoodistoService koodistoService,
                             final HakuService hakuService) {
        this.koodistoService = koodistoService;
        this.hakuService = hakuService;
    }

    @Override
    public List<ApplicationSystem> generate() {
        List<ApplicationSystem> applicationSystems = hakuService.getApplicationSystems();
        List<ApplicationSystem> asList = Lists.newArrayList();
        for (ApplicationSystem as : applicationSystems) {
            Form form = Yhteishaku2013.generateForm(as, koodistoService);
            asList.add(new ApplicationSystem(as.getId(), form, as.getName(), as.getApplicationPeriods(), as.getApplicationSystemType()));
        }
        return asList;
    }




}
