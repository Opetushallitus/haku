package fi.vm.sade.haku.oppija.hakemus.it;
import static org.junit.Assert.assertEquals;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoImpl;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.impl.ApplicationSystemServiceImpl;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import org.junit.Test;


public class ValidationIT extends IntegrationTestSupport {
    @Test
    public void validateFixture() {
        final ApplicationSystemService ass = appContext.getBean(ApplicationSystemServiceImpl.class);
        final ElementTreeValidator validator = appContext.getBean(ElementTreeValidator.class);
        final ApplicationDAOMongoImpl dao = appContext.getBean(ApplicationDAOMongoImpl.class);

        ApplicationSystem applicationSystemFixture = ass.getApplicationSystem("1.2.246.562.5.2014022711042555034240");
        Element phase = ElementTree.getFirstChild(applicationSystemFixture.getForm());
        Application applicationFixture = dao.find(new Application()).get(0);
        ValidationResult vr = validator.validate(new ValidationInput(phase, applicationFixture.getVastauksetMerged(), applicationFixture.getOid(), applicationSystemFixture.getId()));
        assertEquals(vr.getErrorMessages().size(), 2);
    }
}
