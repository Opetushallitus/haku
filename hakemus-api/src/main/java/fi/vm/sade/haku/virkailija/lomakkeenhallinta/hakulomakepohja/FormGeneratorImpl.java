package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
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
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FormGeneratorImpl implements FormGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormGeneratorImpl.class);

    private final HakuService hakuService;
    private final FormConfigurationService formConfigurationService;

    @Autowired
    public FormGeneratorImpl(final HakuService hakuService,
                             final FormConfigurationService formConfigurationService) {
        this.hakuService = hakuService;
        this.formConfigurationService = formConfigurationService;
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
        return new ApplicationSystemBuilder()
                .setId(as.getId())
                .setForm(generateForm(formParameters))
                .setName(as.getName())
                .setState(as.getState())
                .setApplicationPeriods(as.getApplicationPeriods())
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
                .setLastGenerated(new Date())
                .setAllowedLanguages(formParameters.getAllowedLanguages())
                .get();
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
