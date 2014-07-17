package fi.vm.sade.haku.oppija.hakemus.it;
import static org.junit.Assert.assertEquals;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoImpl;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.impl.ApplicationSystemServiceImpl;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;


public class ValidationIT extends IntegrationTestSupport {
    final ApplicationSystemService ass = appContext.getBean(ApplicationSystemServiceImpl.class);
    final ApplicationDAOMongoImpl dao = appContext.getBean(ApplicationDAOMongoImpl.class);
    final ElementTreeValidator validator = appContext.getBean(ElementTreeValidator.class);
    final String applicationSystemId = "1.2.246.562.5.2014022711042555034240";

    @Test
    public void validateFixture() {
        ApplicationSystem applicationSystemFixture = ass.getApplicationSystem(applicationSystemId);
        Application applicationFixture = dao.find(new Application()).get(0);
        assertEquals(validateApplication(applicationSystemFixture, applicationFixture).size(), 1);
    }

    @Test
    public void validateUpdatedFixture() {
        ApplicationSystem applicationSystemFixture = ass.getApplicationSystem(applicationSystemId);
        Application applicationFixture = dao.find(new Application()).get(0);
        Map<String, String> vastaukset = new HashMap<String, String>();
        vastaukset.put("preference1-Koulutus-id", "1.2.246.562.20.30500448839");
        vastaukset.put("preference1-Opetuspiste-id", "1.2.246.562.10.35241670047");
        applicationFixture.addVaiheenVastaukset("hakutoiveet", vastaukset);
        assertEquals(validateApplication(applicationSystemFixture, applicationFixture).size(), 6);
    }

    private Map<String, I18nText> validateApplication(ApplicationSystem applicationSystemFixture, Application applicationFixture) {
        ValidationResult vr = validator.validate(new ValidationInput(applicationSystemFixture.getForm(), applicationFixture.getVastauksetMerged(), applicationFixture.getOid(), applicationSystemFixture.getId()));
        return vr.getErrorMessages();
    }
}
