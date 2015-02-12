package fi.vm.sade.haku.oppija.hakemus.it;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class ValidationIT extends IntegrationTestSupport {
    final ElementTreeValidator validator = appContext.getBean(ElementTreeValidator.class);

    @Test
    public void validateFixture() {
        assertEquals(validateApplication(getTestApplicationSystem(), getTestApplication()).size(), 0);
    }

    @Test
    public void validateUpdatedFixture() {
        Application applicationFixture = getTestApplication();
        Map<String, String> vastaukset = new HashMap<String, String>();
        vastaukset.put("preference1-Koulutus-id", "1.2.246.562.20.30500448839");
        vastaukset.put("preference1-Opetuspiste-id", "1.2.246.562.10.35241670047");
        applicationFixture.addVaiheenVastaukset("hakutoiveet", vastaukset);
        assertEquals(validateApplication(getTestApplicationSystem(), applicationFixture).size(), 5);
    }

    private Map<String, I18nText> validateApplication(ApplicationSystem applicationSystemFixture, Application applicationFixture) {
        ValidationResult vr = validator.validate(new ValidationInput(applicationSystemFixture.getForm(),
                applicationFixture.getVastauksetMerged(),
                applicationFixture.getOid(),
                applicationSystemFixture.getId(),
            ValidationInput.ValidationContext.officer_modify));
        return vr.getErrorMessages();
    }
}
