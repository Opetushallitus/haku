package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.hakutoiveet.HakutoiveetPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.henkilotiedot.HenkilotiedotPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta.KoulutustaustaPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot.LisatiedotPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.OsaaminenPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.valmis.ValmisPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.FormConfigurationService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FormGeneratorImpl implements FormGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormGeneratorImpl.class);

    private final HakuService hakuService;
    private final FormConfigurationService formConfigurationService;

    private final boolean demoMode;
    private final List<String> demoOids = new ArrayList<>();

    @Autowired
    public FormGeneratorImpl(final HakuService hakuService,
                             final FormConfigurationService formConfigurationService,
                             @Value("${mode.demo:false}") boolean demoMode,
                             @Value("${demo.hakuoids}") String demoOids) {
        this.hakuService = hakuService;
        this.formConfigurationService = formConfigurationService;
        this.demoMode = demoMode;

        if(demoOids != null) {
            this.demoOids.addAll(Arrays.asList(demoOids.split(",")));
        }
    }

    @Override
    public ApplicationSystem generate(String oid) {
        ApplicationSystem as = hakuService.getApplicationSystem(oid);
        return createApplicationSystem(as);
    }

    @Override
    public Form generateFormWithThemesOnly(String oid) {
        ApplicationSystem as = hakuService.getApplicationSystem(oid);
        FormParameters formParameters = formConfigurationService.getFormParameters(as);
        formParameters.setOnlyThemeGenerationForFormEditor(Boolean.TRUE);
        return generateForm(formParameters);
    }

    private ApplicationSystem createApplicationSystem(ApplicationSystem as) {
        FormParameters formParameters = formConfigurationService.getFormParameters(as);
        ApplicationSystemBuilder appBuilder =  new ApplicationSystemBuilder()
                .setId(as.getId())
                .setForm(generateForm(formParameters))
                .setName(as.getName())
                .setState(as.getState())
                .setApplicationSystemType(as.getApplicationSystemType())
                .setUsePriorities(as.isUsePriorities())
                .setHakutapa(as.getHakutapa())
                .setHakukausiUri(as.getHakukausiUri())
                .setHakukausiVuosi(as.getHakukausiVuosi())
                .setKohdejoukkoUri(as.getKohdejoukkoUri())
                .addApplicationCompleteElements(ValmisPhase.create(formParameters))
                .setMaxApplicationOptions(as.getMaxApplicationOptions())
                .addAdditionalInformationElements(ValmisPhase.createAdditionalInformationElements(formParameters))
                .addApplicationOptionAttachmentRequests(formParameters.getThemeQuestionConfigurator().findAndConfigureAttachmentRequests())
                .addAttachmentGroupAddresses(formParameters.getAttachmentGroupConfigurator().configureAttachmentGroupAddresses())
                .setLastGenerated(new Date())
                .setAosForAutomaticEligibility(as.getAosForAutomaticEligibility())
                .setAllowedLanguages(formParameters.getAllowedLanguages());

        if(demoMode && demoOids.contains(as.getId())) {
            List<ApplicationPeriod> demoPeriods = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -6);
            Date start = cal.getTime();
            cal.add(Calendar.MONTH, 12);
            Date end = cal.getTime();
            demoPeriods.add(new ApplicationPeriod(start, end));
            appBuilder = appBuilder.setApplicationPeriods(demoPeriods);
        } else {
            appBuilder = appBuilder.setApplicationPeriods(as.getApplicationPeriods());
        }

        return appBuilder.get();
    }

    public static Form generateForm(final FormParameters formParameters) {
        ApplicationSystem as = formParameters.getApplicationSystem();
        Form form = new Form(as.getId(), as.getName());
        form.addChild(HenkilotiedotPhase.create(formParameters));
        form.addChild(KoulutustaustaPhase.create(formParameters));
        form.addChild(HakutoiveetPhase.create(formParameters));
        form.addChild(OsaaminenPhase.create(formParameters));
        form.addChild(LisatiedotPhase.create(formParameters));
        return form;
    }
}
