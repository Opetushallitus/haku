package fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja;

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.hakutoiveet.HakutoiveetPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.henkilotiedot.HenkilotiedotPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.koulutustausta.KoulutustaustaPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.lisatiedot.LisatiedotPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.osaaminen.OsaaminenPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.valmis.ValmisPhase;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormGeneratorImpl implements FormGenerator {
    private final KoodistoService koodistoService;
    private final HakuService hakuService;
    private final ThemeQuestionDAO themeQuestionDAO;
    private final HakukohdeService hakukohdeService;
    private final ApplicationOptionService applicationOptionService;

    @Autowired
    public FormGeneratorImpl(final KoodistoService koodistoService, final HakuService hakuService,
      ThemeQuestionDAO themeQuestionDAO, HakukohdeService hakukohdeService, ApplicationOptionService applicationOptionService) {
        this.koodistoService = koodistoService;
        this.hakuService = hakuService;
        this.themeQuestionDAO = themeQuestionDAO;
        this.hakukohdeService = hakukohdeService;
        this.applicationOptionService = applicationOptionService;
    }

    @Override
    public ApplicationSystem generate(String oid) {
        ApplicationSystem as = hakuService.getApplicationSystem(oid);
        return createApplicationSystem(as);
    }

    private ApplicationSystem createApplicationSystem(ApplicationSystem as) {
        FormParameters formParameters = new FormParameters(as, koodistoService, themeQuestionDAO, hakukohdeService, applicationOptionService);
        return new ApplicationSystemBuilder().addId(as.getId()).addForm(generateForm(formParameters))
                .addName(as.getName()).addApplicationPeriods(as.getApplicationPeriods())
                .addApplicationSystemType(as.getApplicationSystemType())
                .addHakukausiUri(as.getHakukausiUri())
                .addHakukausiVuosi(as.getHakukausiVuosi())
                .addKohdejoukkoUri(as.getKohdejoukkoUri())
                .addApplicationCompleteElements(ValmisPhase.create(formParameters))
                .addAdditionalInformationElements(ValmisPhase.createAdditionalInformationElements(formParameters))
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
