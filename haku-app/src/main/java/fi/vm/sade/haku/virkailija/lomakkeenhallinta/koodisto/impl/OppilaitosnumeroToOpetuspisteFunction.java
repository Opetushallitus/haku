package fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl;

import com.google.common.base.Function;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodistoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OppilaitosnumeroToOpetuspisteFunction implements Function<KoodiType, Option> {

    private static Logger log = LoggerFactory.getLogger(OppilaitosnumeroToOpetuspisteFunction.class);

    private final KoodistoClient koodiService;
    private final OrganizationService organisaatioService;

    private static final String LUKIO = "15";
    private static final String LUKIO_JA_PERUSKOULU = "19";
    private static final String OPPILAITOSTYYPPI_LUKIO = "oppilaitostyyppi_15";
    private static final String OPPILAITOSTYYPPI_PK_JA_LUKIO = "oppilaitostyyppi_19";

    public OppilaitosnumeroToOpetuspisteFunction(KoodistoClient koodiService, OrganizationService organisaatioService) {
        log.debug("Creating OppilaitosnumeroToOpetuspisteFunction");
        this.koodiService = koodiService;
        this.organisaatioService = organisaatioService;
    }

    @Override
    public Option apply(KoodiType input) {
        String oppilaitosnumero = input.getKoodiArvo();
        Organization org = organisaatioService.findByOppilaitosnumero(oppilaitosnumero);
        Option opt = new Option(org.getName(), org.getOid());
        return opt;
    }
}
