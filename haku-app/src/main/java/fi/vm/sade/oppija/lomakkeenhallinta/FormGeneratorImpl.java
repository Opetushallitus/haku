package fi.vm.sade.oppija.lomakkeenhallinta;

import com.google.common.collect.Lists;
import fi.vm.sade.oppija.common.koodisto.KoodistoService;
import fi.vm.sade.oppija.common.tarjonta.HakuService;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.LisahakuSyksy;
import fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.YhteishakuKevat;
import fi.vm.sade.oppija.lomakkeenhallinta.hakulomakepohja.YhteishakuSyksy;
import fi.vm.sade.oppija.lomakkeenhallinta.util.OppijaConstants;
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
            Form form = null;
            if (as.getApplicationSystemType().equals(OppijaConstants.LISA_HAKU)) {
                form = LisahakuSyksy.generateForm(as, koodistoService);
            } else {
                if (as.getHakukausiUri().equals(OppijaConstants.HAKUKAUSI_SYKSY)) {
                    form = YhteishakuSyksy.generateForm(as, koodistoService);
                } else if (as.getHakukausiUri().equals(OppijaConstants.HAKUKAUSI_KEVAT)) {
                    form = YhteishakuKevat.generateForm(as, koodistoService);
                } else {
                    //skip
                    continue;
                }
            }
            asList.add(new ApplicationSystemBuilder().addId(as.getId()).addForm(form)
                    .addName(as.getName()).addApplicationPeriods(as.getApplicationPeriods())
                    .addApplicationSystemType(as.getApplicationSystemType())
                    .addHakukausiUri(as.getHakukausiUri())
                    .addHakukausiVuosi(as.getHakukausiVuosi())
                    .get());
        }
        return asList;
    }


}
