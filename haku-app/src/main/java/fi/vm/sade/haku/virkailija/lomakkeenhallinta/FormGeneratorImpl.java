package fi.vm.sade.haku.virkailija.lomakkeenhallinta;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.LisahakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.YhteishakuKevat;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.YhteishakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormGeneratorImpl implements FormGenerator {
    private final KoodistoService koodistoService;
    private final HakuService hakuService;
    private final OrganizationService organisaatioService;

    @Autowired
    public FormGeneratorImpl(final KoodistoService koodistoService,
                             final HakuService hakuService,
                             final OrganizationService organisaatioService) {
        this.koodistoService = koodistoService;
        this.hakuService = hakuService;
        this.organisaatioService = organisaatioService;
    }

    @Override
    public List<ApplicationSystem> generate() {
        List<ApplicationSystem> applicationSystems = hakuService.getApplicationSystems();
        List<ApplicationSystem> asList = Lists.newArrayList();
        for (ApplicationSystem as : applicationSystems) {
            Form form = null;
            List<Element> applicationCompleteElements;
            if (as.getApplicationSystemType().equals(OppijaConstants.LISA_HAKU)) {
                form = LisahakuSyksy.generateForm(as, koodistoService);
                applicationCompleteElements = LisahakuSyksy.generateApplicationCompleteElements();
            } else {
                if (as.getHakukausiUri().equals(OppijaConstants.HAKUKAUSI_SYKSY)) {
                    form = YhteishakuSyksy.generateForm(as, koodistoService);
                    applicationCompleteElements = YhteishakuSyksy.createApplicationCompleteElements();
                } else if (as.getHakukausiUri().equals(OppijaConstants.HAKUKAUSI_KEVAT)) {
                    form = YhteishakuKevat.generateForm(as, koodistoService);
                    applicationCompleteElements = YhteishakuKevat.generateApplicationCompleteElements();
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
                    .addApplicationCompleteElements(applicationCompleteElements)
                    .get());
        }
        return asList;
    }


}
