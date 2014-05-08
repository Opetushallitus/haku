package fi.vm.sade.haku.virkailija.lomakkeenhallinta;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.LisahakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.YhteishakuKevat;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.YhteishakuSyksy;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.valmis.ValmisPhase;
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

    @Autowired
    public FormGeneratorImpl(final KoodistoService koodistoService,
                             final HakuService hakuService) {
        this.koodistoService = koodistoService;
        this.hakuService = hakuService;
    }

    @Override
    public ApplicationSystem generateOne(String oid) {
        ApplicationSystem as = hakuService.getApplicationSystem(oid);
        return createApplicationSystem(as);
    }

    @Override
    public List<ApplicationSystem> getApplicationSystems() {
        return hakuService.getApplicationSystems();
    }

    @Override
    public List<ApplicationSystem> generate() {
        List<ApplicationSystem> applicationSystems = hakuService.getApplicationSystems();
        List<ApplicationSystem> asList = Lists.newArrayList();
        for (ApplicationSystem as : applicationSystems) {
            ApplicationSystem created = createApplicationSystem(as);
            if (created != null) {
                asList.add(created);
            }
        }
        return asList;
    }

    private ApplicationSystem createApplicationSystem(ApplicationSystem as) {
        return new ApplicationSystemBuilder().addId(as.getId()).addForm(generateForm(as))
                .addName(as.getName()).addApplicationPeriods(as.getApplicationPeriods())
                .addApplicationSystemType(as.getApplicationSystemType())
                .addHakukausiUri(as.getHakukausiUri())
                .addHakukausiVuosi(as.getHakukausiVuosi())
                .addApplicationCompleteElements(ValmisPhase.create(as))
                .addAdditionalInformationElements(ValmisPhase.createAdditionalInformationElements(getMessageBundleName("form_messages", as)))
                .get();
    }

    private Form generateForm(final ApplicationSystem as) {
        Form form = null;

        FormParameters formParameters = new FormParameters(as, koodistoService);


        if (formParameters.getFormTemplateType().equals(FormParameters.FormTemplateType.LISAHAKU_SYKSY)) {
            form = LisahakuSyksy.generateForm(formParameters);
        } else {
            if (formParameters.getFormTemplateType().equals(FormParameters.FormTemplateType.YHTEISHAKU_SYKSY)) {
                form = YhteishakuSyksy.generateForm(formParameters);
            } else if (formParameters.getFormTemplateType().equals(FormParameters.FormTemplateType.YHTEISHAKU_KEVAT)) {
                form = YhteishakuKevat.generateForm(formParameters);
            } else {
                return null;
            }
        }
        return form;
    }


    public static String getMessageBundleName(final String baseName, final ApplicationSystem as) {
        String hakutyyppi = OppijaConstants.LISA_HAKU.equals(as.getApplicationSystemType()) ? "lisahaku" : "yhteishaku";
        String hakukausi = OppijaConstants.HAKUKAUSI_SYKSY.equals(as.getHakukausiUri()) ? "syksy" : "kevat";
        return Joiner.on('_').join(baseName, hakutyyppi, hakukausi);

    }


}
