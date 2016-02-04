package fi.vm.sade.haku.oppija.lomake.it.domain;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.And;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Any;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Equals;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.hakutest.IntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration.FeatureFlag.koulutusasteURI;
import static org.junit.Assert.assertEquals;

public class KoulutusasteURITest extends IntegrationTest {
    public static final String AFFECTED_APPLICATION_SYSTEM_ID = "1.2.246.562.29.80306203979";

    @Before
    public void setup() {
        mongoServer.dropCollections();
        applicationSystemService.getCache().invalidateAll();
    }

    @Test
    public void testNewFormGenerationUpgradesRules() {
        applicationSystemService.save(formGenerator.generate(AFFECTED_APPLICATION_SYSTEM_ID));
        assertEquals(true, formConfigurationDAO.findByApplicationSystem(AFFECTED_APPLICATION_SYSTEM_ID).getFeatureFlag(koulutusasteURI));

        ApplicationSystem as = applicationSystemService.getApplicationSystem(AFFECTED_APPLICATION_SYSTEM_ID);
        assertEquals("koulutusasteoph2002_32", getEducationDegreeValue((Any) getEducationDegreeExpr(getTyokokemusRule(getLisatiedotQuestions(as)))));
    }

    @Test
    public void testOldFormGenerationDoesNotUpgradeRules() {
        // Store configuration beforehand to see that re-generation will not overwrite it
        // Emulates old forms that will not be migrated but must still be re-generable
        Map<FormConfiguration.FeatureFlag, Boolean> flags = new HashMap<>();
        flags.put(koulutusasteURI, false);
        formConfigurationDAO.save(new FormConfiguration(AFFECTED_APPLICATION_SYSTEM_ID, FormConfiguration.FormTemplateType.YHTEISHAKU_SYKSY, flags));
        applicationSystemService.save(formGenerator.generate(AFFECTED_APPLICATION_SYSTEM_ID));
        assertEquals(false, formConfigurationDAO.findByApplicationSystem(AFFECTED_APPLICATION_SYSTEM_ID).getFeatureFlag(koulutusasteURI));

        ApplicationSystem as = applicationSystemService.getApplicationSystem(AFFECTED_APPLICATION_SYSTEM_ID);
        assertEquals("32", getEducationDegreeValue((Any) getEducationDegreeExpr(getTyokokemusRule(getLisatiedotQuestions(as)))));
    }

    private static String getEducationDegreeValue(Any educationDegreeExpr) {
        return ((Equals) educationDegreeExpr.children().get(0)).getRight().getValue();
    }

    private static Expr getEducationDegreeExpr(RelatedQuestionRule tyokokemusRule) {
        return ((And) ((And) tyokokemusRule.getExpr()).getRight()).getLeft();
    }

    private static RelatedQuestionRule getTyokokemusRule(List<Element> lisatiedotQuestions) {
        return (RelatedQuestionRule) lisatiedotQuestions.get(0);
    }

    private static List<Element> getLisatiedotQuestions(ApplicationSystem as) {
        return as.getForm().getChildren().get(4).getChildren();
    }
}
